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
  private Boolean mSchedulerMakeSchedulerThreadDaemon;
  private Boolean mSchedulerThreadsInheritContextClassLoaderOfInitializer;
  private Long mSchedulerIdleWaitTime;
  private Long mSchedulerDBFailureRetryInterval;
  private String mSchedulerClassLoadHelperClass;
  private String mSchedulerJobFactoryClass;
  private Boolean mSchedulerSkipUpdateCheck;
  private Integer mSchedulerBatchTriggerAcquisitionMaxCount;
  private Long mSchedulerBatchTriggerAcquisitionFireAheadTimeWindow;
  private String mThreadPoolClass;
  private Integer mThreadPoolThreadCount;
  private Integer mThreadPoolThreadPriority;
  private String mJobStoreClass;
  private String mJobStoreDriverDelegateClass;
  private String mJobStoreDataSource;
  private String mJobStoreTablePrefix;
  private Boolean mJobStoreUseProperties;
  private Integer mJobStoreMisfireThreshold;
  private Boolean mJobStoreIsClustered;
  private Long mJobStoreClusterCheckinInterval;
  private Integer mJobStoreMaxMisfiresToHandleAtATime;
  private Boolean mJobStoreDontSetAutoCommitFalse;
  private String mJobStoreSelectWithLockSQL;
  private Boolean mJobStoreTxIsolationLevelSerializable;
  private Boolean mJobStoreAcquireTriggersWithinLock;
  private String mJobStoreLockHandlerClass;
  private String mJobStoreDriverDelegateInitString;
  private EntityManagerFactory mEntityManagerFactory;

  public Properties getConfig() {

    Properties config = new Properties();

    if (null != mSchedulerInstanceName) {
      config.put("org.quartz.scheduler.instanceName", mSchedulerInstanceName);
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
    }
    if (null != mThreadPoolThreadCount) {
      config.put("org.quartz.threadPool.threadCount", mThreadPoolThreadCount);
    }
    if (null != mThreadPoolThreadPriority) {
      config.put("org.quartz.threadPool.threadPriority", mThreadPoolThreadPriority);
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
      Map<String,Object> em = mEntityManagerFactory.getProperties();

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

  public Boolean getSchedulerMakeSchedulerThreadDaemon() {
    return mSchedulerMakeSchedulerThreadDaemon;
  }

  public void setSchedulerMakeSchedulerThreadDaemon(Boolean pSchedulerMakeSchedulerThreadDaemon) {
    this.mSchedulerMakeSchedulerThreadDaemon = pSchedulerMakeSchedulerThreadDaemon;
  }

  public Boolean getSchedulerThreadsInheritContextClassLoaderOfInitializer() {
    return mSchedulerThreadsInheritContextClassLoaderOfInitializer;
  }

  public void setSchedulerThreadsInheritContextClassLoaderOfInitializer(Boolean pSchedulerThreadsInheritContextClassLoaderOfInitializer) {
    this.mSchedulerThreadsInheritContextClassLoaderOfInitializer = pSchedulerThreadsInheritContextClassLoaderOfInitializer;
  }

  public Long getSchedulerIdleWaitTime() {
    return mSchedulerIdleWaitTime;
  }

  public void setSchedulerIdleWaitTime(Long pSchedulerIdleWaitTime) {
    this.mSchedulerIdleWaitTime = pSchedulerIdleWaitTime;
  }

  public Long getSchedulerDBFailureRetryInterval() {
    return mSchedulerDBFailureRetryInterval;
  }

  public void setSchedulerDBFailureRetryInterval(Long pSchedulerDBFailureRetryInterval) {
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

  public Boolean getSchedulerSkipUpdateCheck() {
    return mSchedulerSkipUpdateCheck;
  }

  public void setSchedulerSkipUpdateCheck(Boolean pSchedulerSkipUpdateCheck) {
    this.mSchedulerSkipUpdateCheck = pSchedulerSkipUpdateCheck;
  }

  public Integer getSchedulerBatchTriggerAcquisitionMaxCount() {
    return mSchedulerBatchTriggerAcquisitionMaxCount;
  }

  public void setSchedulerBatchTriggerAcquisitionMaxCount(Integer pSchedulerBatchTriggerAcquisitionMaxCount) {
    this.mSchedulerBatchTriggerAcquisitionMaxCount = pSchedulerBatchTriggerAcquisitionMaxCount;
  }

  public Long getSchedulerBatchTriggerAcquisitionFireAheadTimeWindow() {
    return mSchedulerBatchTriggerAcquisitionFireAheadTimeWindow;
  }

  public void setSchedulerBatchTriggerAcquisitionFireAheadTimeWindow(Long pSchedulerBatchTriggerAcquisitionFireAheadTimeWindow) {
    this.mSchedulerBatchTriggerAcquisitionFireAheadTimeWindow = pSchedulerBatchTriggerAcquisitionFireAheadTimeWindow;
  }

  public String getThreadPoolClass() {
    return mThreadPoolClass;
  }

  public void setThreadPoolClass(String pThreadPoolClass) {
    this.mThreadPoolClass = pThreadPoolClass;
  }

  public Integer getThreadPoolThreadCount() {
    return mThreadPoolThreadCount;
  }

  public void setThreadPoolThreadCount(Integer pThreadPoolThreadCount) {
    this.mThreadPoolThreadCount = pThreadPoolThreadCount;
  }

  public Integer getThreadPoolThreadPriority() {
    return mThreadPoolThreadPriority;
  }

  public void setThreadPoolThreadPriority(Integer pThreadPoolThreadPriority) {
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

  public Boolean getJobStoreUseProperties() {
    return mJobStoreUseProperties;
  }

  public void setJobStoreUseProperties(Boolean pJobStoreUseProperties) {
    this.mJobStoreUseProperties = pJobStoreUseProperties;
  }

  public Integer getJobStoreMisfireThreshold() {
    return mJobStoreMisfireThreshold;
  }

  public void setJobStoreMisfireThreshold(Integer pJobStoreMisfireThreshold) {
    this.mJobStoreMisfireThreshold = pJobStoreMisfireThreshold;
  }

  public Boolean getJobStoreIsClustered() {
    return mJobStoreIsClustered;
  }

  public void setJobStoreIsClustered(Boolean pJobStoreIsClustered) {
    this.mJobStoreIsClustered = pJobStoreIsClustered;
  }

  public Long getJobStoreClusterCheckinInterval() {
    return mJobStoreClusterCheckinInterval;
  }

  public void setJobStoreClusterCheckinInterval(Long pJobStoreClusterCheckinInterval) {
    this.mJobStoreClusterCheckinInterval = pJobStoreClusterCheckinInterval;
  }

  public Integer getJobStoreMaxMisfiresToHandleAtATime() {
    return mJobStoreMaxMisfiresToHandleAtATime;
  }

  public void setJobStoreMaxMisfiresToHandleAtATime(Integer pJobStoreMaxMisfiresToHandleAtATime) {
    this.mJobStoreMaxMisfiresToHandleAtATime = pJobStoreMaxMisfiresToHandleAtATime;
  }

  public Boolean getJobStoreDontSetAutoCommitFalse() {
    return mJobStoreDontSetAutoCommitFalse;
  }

  public void setJobStoreDontSetAutoCommitFalse(Boolean pJobStoreDontSetAutoCommitFalse) {
    this.mJobStoreDontSetAutoCommitFalse = pJobStoreDontSetAutoCommitFalse;
  }

  public String getJobStoreSelectWithLockSQL() {
    return mJobStoreSelectWithLockSQL;
  }

  public void setJobStoreSelectWithLockSQL(String pJobStoreSelectWithLockSQL) {
    this.mJobStoreSelectWithLockSQL = pJobStoreSelectWithLockSQL;
  }

  public Boolean getJobStoreTxIsolationLevelSerializable() {
    return mJobStoreTxIsolationLevelSerializable;
  }

  public void setJobStoreTxIsolationLevelSerializable(Boolean pJobStoreTxIsolationLevelSerializable) {
    this.mJobStoreTxIsolationLevelSerializable = pJobStoreTxIsolationLevelSerializable;
  }

  public Boolean getJobStoreAcquireTriggersWithinLock() {
    return mJobStoreAcquireTriggersWithinLock;
  }

  public void setJobStoreAcquireTriggersWithinLock(Boolean pJobStoreAcquireTriggersWithinLock) {
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
