package lab.jayway.logging.test;

import junit.framework.TestCase;
import lab.jayway.logging.internal.db.LogDb;
import lab.jayway.logging.service.LogUploader;

public class LogUploaderTest extends TestCase {

	public void test_Nothing_Happens_When_Started_By_AlarmManager_And_Db_Is_Empty() {
		EmptyLogDb logDb = new EmptyLogDb(); 
		LogUploader logUploader = new LogUploader(null, logDb, null, null);
		logUploader.uploadIfNecessary(true);
	}
	
	private static class EmptyLogDb extends LogDb {

		public EmptyLogDb() {
			super(null);
		}
		
		@Override
		public void open() {
		}
		
		@Override
		public boolean isEmpty() {
			return true;
		}
		
		@Override
		public void close() {
		}
		
	}

}
