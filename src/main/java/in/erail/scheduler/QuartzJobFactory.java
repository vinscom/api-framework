package in.erail.scheduler;

import com.google.common.base.Strings;
import in.erail.glue.Glue;
import in.erail.glue.common.Util;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 *
 * @author vinay
 */
public class QuartzJobFactory implements JobFactory {

  public static final String COMPONENT_PATH = "_comp";

  private boolean warnIfNotFound = false;
  private boolean throwIfNotFound = false;
  private Logger mLog = LogManager.getLogger(QuartzJobFactory.class);

  @Override
  public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
    JobDataMap data = bundle.getJobDetail().getJobDataMap();
    Optional<String> compPath = Optional
            .ofNullable(data.getString(COMPONENT_PATH))
            .filter(t -> !Strings.isNullOrEmpty(t));

    Job job;

    if (compPath.isPresent()) {
      job = Glue.instance().resolve(compPath.get());
    } else {
      job = (Job) Util.createInstance(bundle.getJobDetail().getJobClass());
      getLog().warn(() -> "Create job instance from class, Instead of Component. Are you sure, you don't want to use Component:" + bundle.getJobDetail().getJobClass().getCanonicalName());
    }

    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.putAll(scheduler.getContext());
    jobDataMap.putAll(bundle.getJobDetail().getJobDataMap());
    jobDataMap.putAll(bundle.getTrigger().getJobDataMap());

    setBeanProps(job, jobDataMap);

    return job;
  }

  protected void setBeanProps(Object obj, JobDataMap data) throws SchedulerException {

    BeanInfo bi = null;
    try {
      bi = Introspector.getBeanInfo(obj.getClass());
    } catch (IntrospectionException e) {
      handleError("Unable to introspect Job class.", e);
    }

    PropertyDescriptor[] propDescs = bi.getPropertyDescriptors();

    // Get the wrapped entry set so don't have to incur overhead of wrapping for
    // dirty flag checking since this is read only access
    for (Iterator<?> entryIter = data.getWrappedMap().entrySet().iterator(); entryIter.hasNext();) {
      Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entryIter.next();

      String name = (String) entry.getKey();
      String c = name.substring(0, 1).toUpperCase(Locale.US);
      String methName = "set" + c + name.substring(1);

      java.lang.reflect.Method setMeth = getSetMethod(methName, propDescs);

      Class<?> paramType = null;
      Object o = null;

      try {
        if (setMeth == null) {
          handleError(
                  "No setter on Job class " + obj.getClass().getName()
                  + " for property '" + name + "'");
          continue;
        }

        paramType = setMeth.getParameterTypes()[0];
        o = entry.getValue();

        if (o == null) {
          continue;
        }

        Object parm = null;
        if (paramType.isPrimitive()) {
          if (paramType.equals(int.class)) {
            if (o instanceof String) {
              parm = Integer.valueOf((String) o);
            } else if (o instanceof Integer) {
              parm = o;
            }
          } else if (paramType.equals(long.class)) {
            if (o instanceof String) {
              parm = Long.valueOf((String) o);
            } else if (o instanceof Long) {
              parm = o;
            }
          } else if (paramType.equals(float.class)) {
            if (o instanceof String) {
              parm = Float.valueOf((String) o);
            } else if (o instanceof Float) {
              parm = o;
            }
          } else if (paramType.equals(double.class)) {
            if (o instanceof String) {
              parm = Double.valueOf((String) o);
            } else if (o instanceof Double) {
              parm = o;
            }
          } else if (paramType.equals(boolean.class)) {
            if (o instanceof String) {
              parm = Boolean.valueOf((String) o);
            } else if (o instanceof Boolean) {
              parm = o;
            }
          } else if (paramType.equals(byte.class)) {
            if (o instanceof String) {
              parm = Byte.valueOf((String) o);
            } else if (o instanceof Byte) {
              parm = o;
            }
          } else if (paramType.equals(short.class)) {
            if (o instanceof String) {
              parm = Short.valueOf((String) o);
            } else if (o instanceof Short) {
              parm = o;
            }
          } else if (paramType.equals(char.class)) {
            if (o instanceof String) {
              String str = (String) o;
              if (str.length() == 1) {
                parm = Character.valueOf(str.charAt(0));
              }
            } else if (o instanceof Character) {
              parm = o;
            }
          }
        } else if ((o != null) && (paramType.isAssignableFrom(o.getClass()))) {
          parm = o;
        }

        // If the parameter wasn't originally null, but we didn't find a 
        // matching parameter, then we are stuck.
        if ((o != null) && (parm == null)) {
          handleError(
                  "The setter on Job class " + obj.getClass().getName()
                  + " for property '" + name
                  + "' expects a " + paramType
                  + " but was given " + o.getClass().getName());
          continue;
        }

        setMeth.invoke(obj, new Object[]{parm});
      } catch (NumberFormatException nfe) {
        handleError(
                "The setter on Job class " + obj.getClass().getName()
                + " for property '" + name
                + "' expects a " + paramType
                + " but was given " + Optional.ofNullable(o).map(p -> p.getClass().getName()).orElse("null"), nfe);
      } catch (IllegalArgumentException e) {
        handleError(
                "The setter on Job class " + obj.getClass().getName()
                + " for property '" + name
                + "' expects a " + paramType
                + " but was given " + Optional.ofNullable(o).map(p -> p.getClass().getName()).orElse("null"), e);
      } catch (IllegalAccessException e) {
        handleError(
                "The setter on Job class " + obj.getClass().getName()
                + " for property '" + name
                + "' could not be accessed.", e);
      } catch (InvocationTargetException e) {
        handleError(
                "The setter on Job class " + obj.getClass().getName()
                + " for property '" + name
                + "' could not be invoked.", e);
      }
    }
  }

  private void handleError(String message) throws SchedulerException {
    handleError(message, null);
  }

  private void handleError(String message, Exception e) throws SchedulerException {
    if (isThrowIfPropertyNotFound()) {
      throw new SchedulerException(message, e);
    }

    if (isWarnIfPropertyNotFound()) {
      if (e == null) {
        getLog().warn(message);
      } else {
        getLog().warn(message, e);
      }
    }
  }

  private java.lang.reflect.Method getSetMethod(String name,
          PropertyDescriptor[] props) {
    for (int i = 0; i < props.length; i++) {
      java.lang.reflect.Method wMeth = props[i].getWriteMethod();

      if (wMeth == null) {
        continue;
      }

      if (wMeth.getParameterTypes().length != 1) {
        continue;
      }

      if (wMeth.getName().equals(name)) {
        return wMeth;
      }
    }

    return null;
  }

  /**
   * Whether the JobInstantiation should fail and throw and exception if a key
   * (name) and value (type) found in the JobDataMap does not correspond to a
   * proptery setter on the Job class.
   *
   * @return Returns the throwIfNotFound.
   */
  public boolean isThrowIfPropertyNotFound() {
    return throwIfNotFound;
  }

  /**
   * Whether the JobInstantiation should fail and throw and exception if a key
   * (name) and value (type) found in the JobDataMap does not correspond to a
   * proptery setter on the Job class.
   *
   * @param throwIfNotFound defaults to <code>false</code>.
   */
  public void setThrowIfPropertyNotFound(boolean throwIfNotFound) {
    this.throwIfNotFound = throwIfNotFound;
  }

  /**
   * Whether a warning should be logged if a key (name) and value (type) found
   * in the JobDataMap does not correspond to a proptery setter on the Job
   * class.
   *
   * @return Returns the warnIfNotFound.
   */
  public boolean isWarnIfPropertyNotFound() {
    return warnIfNotFound;
  }

  /**
   * Whether a warning should be logged if a key (name) and value (type) found
   * in the JobDataMap does not correspond to a proptery setter on the Job
   * class.
   *
   * @param warnIfNotFound defaults to <code>true</code>.
   */
  public void setWarnIfPropertyNotFound(boolean warnIfNotFound) {
    this.warnIfNotFound = warnIfNotFound;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

}
