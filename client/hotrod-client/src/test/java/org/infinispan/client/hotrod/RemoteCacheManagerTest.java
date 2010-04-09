package org.infinispan.client.hotrod;

import org.infinispan.manager.CacheManager;
import org.infinispan.server.hotrod.HotRodServer;
import org.infinispan.test.SingleCacheManagerTest;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Properties;

/**
 * // TODO: Document this
 *
 * @author Mircea.Markus@jboss.com
 * @since 4.1
 */
@Test(testName = "client.hotrod.RemoteCacheManagerTest", groups = "functional", enabled = false, description = "TODO To be re-enabled when we have a multithreaded HotRod server impl")
public class RemoteCacheManagerTest extends SingleCacheManagerTest {

   CacheManager cacheManager = null;
   HotRodServer hotrodServer = null;


   @Override
   protected CacheManager createCacheManager() throws Exception {
      cacheManager = TestCacheManagerFactory.createLocalCacheManager();
      hotrodServer = new HotRodServer();
      hotrodServer.start("127.0.0.1", 11311, cacheManager, 0, 0);
      return cacheManager;
   }

   @AfterTest(alwaysRun = true)
   public void release() {
      if (hotrodServer != null) hotrodServer.stop();
      if (cacheManager != null) cacheManager.stop();
   }

   public void testNoArgConstructor() {
      RemoteCacheManager remoteCacheManager = new RemoteCacheManager();
      assert remoteCacheManager.isStarted();
      assertWorks(remoteCacheManager);
      remoteCacheManager.stop();
   }

   public void testBooleanConstructor() {
      RemoteCacheManager remoteCacheManager = new RemoteCacheManager(false);
      assert !remoteCacheManager.isStarted();
      remoteCacheManager.start();
      assertWorks(remoteCacheManager);
      remoteCacheManager.stop();
   }

   public void testUrlConstructor() throws Exception {
      URL resource = Thread.currentThread().getContextClassLoader().getResource("empty-config.properties");
      assert resource != null;
      RemoteCacheManager remoteCacheManager = new RemoteCacheManager(resource);
      assertWorks(remoteCacheManager);
      remoteCacheManager.stop();
   }
   
   public void testUrlAndBooleanConstructor() throws Exception {
      URL resource = Thread.currentThread().getContextClassLoader().getResource("empty-config.properties");
      assert resource != null;
      RemoteCacheManager remoteCacheManager = new RemoteCacheManager(resource, false);
      assert !remoteCacheManager.isStarted();
      remoteCacheManager.start();
      assertWorks(remoteCacheManager);
      remoteCacheManager.stop();
   }

   public void testPropertiesConstructor() {
      RemoteCacheManager remoteCacheManager = new RemoteCacheManager(new Properties());
      assert remoteCacheManager.isStarted();
      assertWorks(remoteCacheManager);
      remoteCacheManager.stop();
   }

   public void testPropertiesAndBooleanConstructor() {
      RemoteCacheManager remoteCacheManager = new RemoteCacheManager(new Properties(), false);
      assert !remoteCacheManager.isStarted();
      remoteCacheManager.start();
      assertWorks(remoteCacheManager);
      remoteCacheManager.stop();
   }

   private void assertWorks(RemoteCacheManager remoteCacheManager) {
      RemoteCache<Object, Object> cache = remoteCacheManager.getCache();
      assert cache.ping();
      cache.put("aKey", "aValue");
      assert cache.get("aKey").equals("aValue");
   }
}