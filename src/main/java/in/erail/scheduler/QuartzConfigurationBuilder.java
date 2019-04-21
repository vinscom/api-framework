package in.erail.scheduler;

import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author vinay
 */
public class QuartzConfigurationBuilder {

  private String mSchedulerInstanceName;
  private String mSchedulerInstanceId;
  private String mSchedulerInstanceIdGeneratorClass;
  private String mSchedulerThreadName;
  private String mSchedulerMakeSchedulerThreadDaemon;
  private String mSchedulerThreadsInheritContextClassLoaderOfInitializer;
  private String mSchedulerIdleWaitTime;
  private String mSchedulerDBFailureRetryInterval;
  private String mSchedulerClassLoadHelperClass;
  private String mSchedulerJobFactoryClass;
  private String mSchedulerSkipUpdateCheck;
  private String mSchedulerBatchTriggerAcquisitionMaxCount;
  private String mSchedulerBatchTriggerAcquisitionFireAheadTimeWindow;
  private String mThreadPoolClass;
  private String mThreadPoolThreadCount;
  private String mThreadPoolThreadPriority;
  private String mJobStoreClass;
  private String mJobStoreDriverDelegateClass;
  private String mJobStoreDataSource;
  private String mJobStoreTablePrefix;
  private String mJobStoreUseProperties;
  private String mJobStoreMisfireThreshold;
  private String mJobStoreIsClustered;
  private String mJobStoreClusterCheckinInterval;
  private String mJobStoreMaxMisfiresToHandleAtATime;
  private String mJobStoreDontSetAutoCommitFalse;
  private String mJobStoreSelectWithLockSQL;
  private String mJobStoreTxIsolationLevelSerializable;
  private String mJobStoreAcquireTriggersWithinLock;
  private String mJobStoreLockHandlerClass;
  private String mJobStoreDriverDelegateInitString;
  private EntityManagerFactory mEntityManagerFactory;

  public Properties getConfig() {

    Properties config = new Properties();

    if (null != mSchedulerInstanceName) {
      config.put("org.quartz.scheduler.instanceName", mSchedulerInstanceName);
    } else {
      config.put("org.quartz.scheduler.instanceName", "DefaultQuartzScheduler");
    }
    if (null != mSchedulerInstanceId) {
      config.put("org.quartz.scheduler.instanceId", mSchedulerInstanceId);
    }
    if (null != mSchedulerInstanceIdGeneratorClass) {
      config.put("org.quartz.scheduler.instanceIdGenerator.class", mSchedulerInstanceIdGeneratorClass);
    }
    if (null != mSchedulerThreadName) {
      config.put("org.quartz.scheduler.threadName", mSchedulerThreadName);
    }
    if (null != mSchedulerMakeSchedulerThreadDaemon) {
      config.put("org.quartz.scheduler.makeSchedulerThreadDaemon", mSchedulerMakeSchedulerThreadDaemon);
    }
    if (null != mSchedulerThreadsInheritContextClassLoaderOfInitializer) {
      config.put("org.quartz.scheduler.threadsInheritContextClassLoaderOfInitializer", mSchedulerThreadsInheritContextClassLoaderOfInitializer);
    }
    if (null != mSchedulerIdleWaitTime) {
      config.put("org.quartz.scheduler.idleWaitTime", mSchedulerIdleWaitTime);
    }
    if (null != mSchedulerDBFailureRetryInterval) {
      config.put("org.quartz.scheduler.dbFailureRetryInterval", mSchedulerDBFailureRetryInterval);
    }
    if (null != mSchedulerClassLoadHelperClass) {
      config.put("org.quartz.scheduler.classLoadHelper.class", mSchedulerClassLoadHelperClass);
    }
    if (null != mSchedulerJobFactoryClass) {
      config.put("org.quartz.scheduler.jobFactory.class", mSchedulerJobFactoryClass);
    } else {
      config.put("org.quartz.scheduler.jobFactory.class", QuartzJobFactory.class.getCanonicalName());
    }
    if (null != mSchedulerSkipUpdateCheck) {
      config.put("org.quartz.scheduler.skipUpdateCheck", mSchedulerSkipUpdateCheck);
    }
    if (null != mSchedulerBatchTriggerAcquisitionMaxCount) {
      config.put("org.quartz.scheduler.batchTriggerAcquisitionMaxCount", mSchedulerBatchTriggerAcquisitionMaxCount);
    }
    if (null != mSchedulerBatchTriggerAcquisitionFireAheadTimeWindow) {
      config.put("org.quartz.scheduler.batchTriggerAcquisitionFireAheadTimeWindow", mSchedulerBatchTriggerAcquisitionFireAheadTimeWindow);
    }
    if (null != mThreadPoolClass) {
      config.put("org.quartz.threadPool.class", mThreadPoolClass);
    } else {
      config.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
    }

    if (null != mThreadPoolThreadCount) {
      config.put("org.quartz.threadPool.threadCount", mThreadPoolThreadCount);
    } else {
      config.put("org.quartz.threadPool.threadCount", "1");
    }

    if (null != mThreadPoolThreadPriority) {
      config.put("org.quartz.threadPool.threadPriority", mThreadPoolThreadPriority);
    } else {
      config.put("org.quartz.threadPool.threadPriority", "5");
    }

    if (null != mJobStoreDriverDelegateClass) {
      config.put("org.quartz.jobStore.driverDelegateClass", mJobStoreDriverDelegateClass);
    }
    if (null != mJobStoreTablePrefix) {
      config.put("org.quartz.jobStore.tablePrefix", mJobStoreTablePrefix);
    }
    if (null != mJobStoreUseProperties) {
      config.put("org.quartz.jobStore.useProperties", mJobStoreUseProperties);
    }
    if (null != mJobStoreMisfireThreshold) {
      config.put("org.quartz.jobStore.misfireThreshold", mJobStoreMisfireThreshold);
    } else {
      config.put("org.quartz.jobStore.misfireThreshold", "60000");
    }
    if (null != mJobStoreIsClustered) {
      config.put("org.quartz.jobStore.isClustered", mJobStoreIsClustered);
    }
    if (null != mJobStoreClusterCheckinInterval) {
      config.put("org.quartz.jobStore.clusterCheckinInterval", mJobStoreClusterCheckinInterval);
    }
    if (null != mJobStoreMaxMisfiresToHandleAtATime) {
      config.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", mJobStoreMaxMisfiresToHandleAtATime);
    }
    if (null != mJobStoreDontSetAutoCommitFalse) {
      config.put("org.quartz.jobStore.dontSetAutoCommitFalse", mJobStoreDontSetAutoCommitFalse);
    }
    if (null != mJobStoreSelectWithLockSQL) {
      config.put("org.quartz.jobStore.selectWithLockSQL", mJobStoreSelectWithLockSQL);
    }
    if (null != mJobStoreTxIsolationLevelSerializable) {
      config.put("org.quartz.jobStore.txIsolationLevelSerializable", mJobStoreTxIsolationLevelSerializable);
    }
    if (null != mJobStoreAcquireTriggersWithinLock) {
      config.put("org.quartz.jobStore.acquireTriggersWithinLock", mJobStoreAcquireTriggersWithinLock);
    }
    if (null != mJobStoreLockHandlerClass) {
      config.put("org.quartz.jobStore.lockHandler.class", mJobStoreLockHandlerClass);
    }
    if (null != mJobStoreDriverDelegateInitString) {
      config.put("org.quartz.jobStore.driverDelegateInitString", mJobStoreDriverDelegateInitString);
    }

    if (null != mEntityManagerFactory) {
      Map<String, Object> em = mEntityManagerFactory.getProperties();

      String datasourceName = "org.quartz.dataSource.";
      if (mJobStoreDataSource == null) {
        mJobStoreDataSource = "default";
        datasourceName = datasourceName + "default.";
      } else {
        datasourceName = datasourceName + mJobStoreDataSource + ".";
      }

      mJobStoreClass = "org.quartz.impl.jdbcjobstore.JobStoreTX";

      config.put(datasourceName + "driver", em.get("javax.persistence.jdbc.driver"));
      config.put(datasourceName + "URL", em.get("javax.persistence.jdbc.url"));
      config.put(datasourceName + "user", em.get("javax.persistence.jdbc.user"));
      config.put(datasourceName + "password", em.get("javax.persistence.jdbc.password"));
    }

    if (null != mJobStoreDataSource) {
      config.put("org.quartz.jobStore.dataSource", mJobStoreDataSource);
    }

    if (null != mJobStoreClass) {
      config.put("org.quartz.jobStore.class", mJobStoreClass);
    } else {
      config.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
    }

    return config;
  }

  public String getSchedulerInstanceName() {
    return mSchedulerInstanceName;
  }

  public void setSchedulerInstanceName(String pSchedulerInstanceName) {
    this.mSchedulerInstanceName = pSchedulerInstanceName;
  }

  public String getSchedulerInstanceId() {
    return mSchedulerInstanceId;
  }

  public void setSchedulerInstanceId(String pSchedulerInstanceId) {
    this.mSchedulerInstanceId = pSchedulerInstanceId;
  }

  public String getSchedulerInstanceIdGeneratorClass() {
    return mSchedulerInstanceIdGeneratorClass;
  }

  public void setSchedulerInstanceIdGeneratorClass(String pSchedulerInstanceIdGeneratorClass) {
    this.mSchedulerInstanceIdGeneratorClass = pSchedulerInstanceIdGeneratorClass;
  }

  public String getSchedulerThreadName() {
    return mSchedulerThreadName;
  }

  public void setSchedulerThreadName(String pSchedulerThreadName) {
    this.mSchedulerThreadName = pSchedulerThreadName;
  }

  public String getSchedulerMakeSchedulerThreadDaemon() {
    return mSchedulerMakeSchedulerThreadDaemon;
  }

  public void setSchedulerMakeSchedulerThreadDaemon(String pSchedulerMakeSchedulerThreadDaemon) {
    this.mSchedulerMakeSchedulerThreadDaemon = pSchedulerMakeSchedulerThreadDaemon;
  }

  public String getSchedulerThreadsInheritContextClassLoaderOfInitializer() {
    return mSchedulerThreadsInheritContextClassLoaderOfInitializer;
  }

  public void setSchedulerThreadsInheritContextClassLoaderOfInitializer(String pSchedulerThreadsInheritContextClassLoaderOfInitializer) {
    this.mSchedulerThreadsInheritContextClassLoaderOfInitializer = pSchedulerThreadsInheritContextClassLoaderOfInitializer;
  }

  public String getSchedulerIdleWaitTime() {
    return mSchedulerIdleWaitTime;
  }

  public void setSchedulerIdleWaitTime(String pSchedulerIdleWaitTime) {
    this.mSchedulerIdleWaitTime = pSchedulerIdleWaitTime;
  }

  public String getSchedulerDBFailureRetryInterval() {
    return mSchedulerDBFailureRetryInterval;
  }

  public void setSchedulerDBFailureRetryInterval(String pSchedulerDBFailureRetryInterval) {
    this.mSchedulerDBFailureRetryInterval = pSchedulerDBFailureRetryInterval;
  }

  public String getSchedulerClassLoadHelperClass() {
    return mSchedulerClassLoadHelperClass;
  }

  public void setSchedulerClassLoadHelperClass(String pSchedulerClassLoadHelperClass) {
    this.mSchedulerClassLoadHelperClass = pSchedulerClassLoadHelperClass;
  }

  public String getSchedulerJobFactoryClass() {
    return mSchedulerJobFactoryClass;
  }

  public void setSchedulerJobFactoryClass(String pSchedulerJobFactoryClass) {
    this.mSchedulerJobFactoryClass = pSchedulerJobFactoryClass;
  }

  public String getSchedulerSkipUpdateCheck() {
    return mSchedulerSkipUpdateCheck;
  }

  public void setSchedulerSkipUpdateCheck(String pSchedulerSkipUpdateCheck) {
    this.mSchedulerSkipUpdateCheck = pSchedulerSkipUpdateCheck;
  }

  public String getSchedulerBatchTriggerAcquisitionMaxCount() {
    return mSchedulerBatchTriggerAcquisitionMaxCount;
  }

  public void setSchedulerBatchTriggerAcquisitionMaxCount(String pSchedulerBatchTriggerAcquisitionMaxCount) {
    this.mSchedulerBatchTriggerAcquisitionMaxCount = pSchedulerBatchTriggerAcquisitionMaxCount;
  }

  public String getSchedulerBatchTriggerAcquisitionFireAheadTimeWindow() {
    return mSchedulerBatchTriggerAcquisitionFireAheadTimeWindow;
  }

  public void setSchedulerBatchTriggerAcquisitionFireAheadTimeWindow(String pSchedulerBatchTriggerAcquisitionFireAheadTimeWindow) {
    this.mSchedulerBatchTriggerAcquisitionFireAheadTimeWindow = pSchedulerBatchTriggerAcquisitionFireAheadTimeWindow;
  }

  public String getThreadPoolClass() {
    return mThreadPoolClass;
  }

  public void setThreadPoolClass(String pThreadPoolClass) {
    this.mThreadPoolClass = pThreadPoolClass;
  }

  public String getThreadPoolThreadCount() {
    return mThreadPoolThreadCount;
  }

  public void setThreadPoolThreadCount(String pThreadPoolThreadCount) {
    this.mThreadPoolThreadCount = pThreadPoolThreadCount;
  }

  public String getThreadPoolThreadPriority() {
    return mThreadPoolThreadPriority;
  }

  public void setThreadPoolThreadPriority(String pThreadPoolThreadPriority) {
    this.mThreadPoolThreadPriority = pThreadPoolThreadPriority;
  }

  public String getJobStoreClass() {
    return mJobStoreClass;
  }

  public void setJobStoreClass(String pJobStoreClass) {
    this.mJobStoreClass = pJobStoreClass;
  }

  public String getJobStoreDriverDelegateClass() {
    return mJobStoreDriverDelegateClass;
  }

  public void setJobStoreDriverDelegateClass(String pJobStoreDriverDelegateClass) {
    this.mJobStoreDriverDelegateClass = pJobStoreDriverDelegateClass;
  }

  public String getJobStoreDataSource() {
    return mJobStoreDataSource;
  }

  public void setJobStoreDataSource(String pJobStoreDataSource) {
    this.mJobStoreDataSource = pJobStoreDataSource;
  }

  public String getJobStoreTablePrefix() {
    return mJobStoreTablePrefix;
  }

  public void setJobStoreTablePrefix(String pJobStoreTablePrefix) {
    this.mJobStoreTablePrefix = pJobStoreTablePrefix;
  }

  public String getJobStoreUseProperties() {
    return mJobStoreUseProperties;
  }

  public void setJobStoreUseProperties(String pJobStoreUseProperties) {
    this.mJobStoreUseProperties = pJobStoreUseProperties;
  }

  public String getJobStoreMisfireThreshold() {
    return mJobStoreMisfireThreshold;
  }

  public void setJobStoreMisfireThreshold(String pJobStoreMisfireThreshold) {
    this.mJobStoreMisfireThreshold = pJobStoreMisfireThreshold;
  }

  public String getJobStoreIsClustered() {
    return mJobStoreIsClustered;
  }

  public void setJobStoreIsClustered(String pJobStoreIsClustered) {
    this.mJobStoreIsClustered = pJobStoreIsClustered;
  }

  public String getJobStoreClusterCheckinInterval() {
    return mJobStoreClusterCheckinInterval;
  }

  public void setJobStoreClusterCheckinInterval(String pJobStoreClusterCheckinInterval) {
    this.mJobStoreClusterCheckinInterval = pJobStoreClusterCheckinInterval;
  }

  public String getJobStoreMaxMisfiresToHandleAtATime() {
    return mJobStoreMaxMisfiresToHandleAtATime;
  }

  public void setJobStoreMaxMisfiresToHandleAtATime(String pJobStoreMaxMisfiresToHandleAtATime) {
    this.mJobStoreMaxMisfiresToHandleAtATime = pJobStoreMaxMisfiresToHandleAtATime;
  }

  public String getJobStoreDontSetAutoCommitFalse() {
    return mJobStoreDontSetAutoCommitFalse;
  }

  public void setJobStoreDontSetAutoCommitFalse(String pJobStoreDontSetAutoCommitFalse) {
    this.mJobStoreDontSetAutoCommitFalse = pJobStoreDontSetAutoCommitFalse;
  }

  public String getJobStoreSelectWithLockSQL() {
    return mJobStoreSelectWithLockSQL;
  }

  public void setJobStoreSelectWithLockSQL(String pJobStoreSelectWithLockSQL) {
    this.mJobStoreSelectWithLockSQL = pJobStoreSelectWithLockSQL;
  }

  public String getJobStoreTxIsolationLevelSerializable() {
    return mJobStoreTxIsolationLevelSerializable;
  }

  public void setJobStoreTxIsolationLevelSerializable(String pJobStoreTxIsolationLevelSerializable) {
    this.mJobStoreTxIsolationLevelSerializable = pJobStoreTxIsolationLevelSerializable;
  }

  public String getJobStoreAcquireTriggersWithinLock() {
    return mJobStoreAcquireTriggersWithinLock;
  }

  public void setJobStoreAcquireTriggersWithinLock(String pJobStoreAcquireTriggersWithinLock) {
    this.mJobStoreAcquireTriggersWithinLock = pJobStoreAcquireTriggersWithinLock;
  }

  public String getJobStoreLockHandlerClass() {
    return mJobStoreLockHandlerClass;
  }

  public void setJobStoreLockHandlerClass(String pJobStoreLockHandlerClass) {
    this.mJobStoreLockHandlerClass = pJobStoreLockHandlerClass;
  }

  public String getJobStoreDriverDelegateInitString() {
    return mJobStoreDriverDelegateInitString;
  }

  public void setJobStoreDriverDelegateInitString(String pJobStoreDriverDelegateInitString) {
    this.mJobStoreDriverDelegateInitString = pJobStoreDriverDelegateInitString;
  }

  public EntityManagerFactory getEntityManagerFactory() {
    return mEntityManagerFactory;
  }

  public void setEntityManagerFactory(EntityManagerFactory pEntityManagerFactory) {
    this.mEntityManagerFactory = pEntityManagerFactory;
  }

}
