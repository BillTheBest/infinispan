package org.infinispan.configuration.cache;

import java.util.Properties;

import org.infinispan.loaders.CacheLoader;
import org.infinispan.util.TypedProperties;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

public class LoaderConfigurationBuilder extends AbstractLoadersConfigurationChildBuilder<LoaderConfiguration> implements
      LoaderConfigurationChildBuilder {

   private static final Log log = LogFactory.getLog(LoaderConfigurationBuilder.class);

   private CacheLoader cacheLoader;
   private boolean fetchPersistentState = false;
   private boolean ignoreModifications = false;
   private boolean purgeOnStartup = false;
   private int purgerThreads = 1;
   private boolean purgeSynchronously = false;
   private final AsyncLoaderConfigurationBuilder async;
   private final SingletonStoreConfigurationBuilder singletonStore;
   private Properties properties = new Properties();

   LoaderConfigurationBuilder(LoadersConfigurationBuilder builder) {
      super(builder);
      this.async = new AsyncLoaderConfigurationBuilder(this);
      this.singletonStore = new SingletonStoreConfigurationBuilder(this);
   }

   public LoaderConfigurationBuilder cacheLoader(CacheLoader cacheLoader) {
      this.cacheLoader = cacheLoader;
      return this;
   }

   public LoaderConfigurationBuilder fetchPersistentState(boolean b) {
      this.fetchPersistentState = b;
      return this;
   }

   public LoaderConfigurationBuilder ignoreModifications(boolean b) {
      this.ignoreModifications = b;
      return this;
   }

   public LoaderConfigurationBuilder purgeOnStartup(boolean b) {
      this.purgeOnStartup = b;
      return this;
   }

   public LoaderConfigurationBuilder purgerThreads(int i) {
      this.purgerThreads = i;
      return this;
   }

   public LoaderConfigurationBuilder purgeSynchronously(boolean b) {
      this.purgeSynchronously = b;
      return this;
   }

   /**
    * <p>
    * Defines a single property. Can be used multiple times to define all needed properties, but the
    * full set is overridden by {@link #withProperties(Properties)}.
    * </p>
    * <p>
    * These properties are passed directly to the cache loader.
    * </p>
    */
   public LoaderConfigurationBuilder addProperty(String key, String value) {
      this.properties.put(key, value);
      return this;
   }

   /**
    * <p>
    * These properties are passed directly to the cache loader.
    * </p>
    */
   public LoaderConfigurationBuilder withProperties(Properties props) {
      this.properties = props;
      return this;
   }

   @Override
   void validate() {
      async.validate();
      singletonStore.validate();
      if (!getLoadersBuilder().shared() && fetchPersistentState && purgeOnStartup
            && getBuilder().clustering().cacheMode().isClustered())
         log.staleEntriesWithoutFetchPersistentStateOrPurgeOnStartup();
   }

   @Override
   LoaderConfiguration create() {
      return new LoaderConfiguration(TypedProperties.toTypedProperties(properties), cacheLoader, fetchPersistentState,
            ignoreModifications, purgeOnStartup, purgerThreads, purgeSynchronously, async.create(), singletonStore.create());
   }

   @Override
   public AsyncLoaderConfigurationBuilder async() {
      return async;
   }

   @Override
   public SingletonStoreConfigurationBuilder singletonStore() {
      return singletonStore;
   }

}
