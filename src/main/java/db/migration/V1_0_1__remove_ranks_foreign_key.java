package db.migration;

import java.sql.Statement;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V1_0_1__remove_ranks_foreign_key extends BaseJavaMigration{

	@Override
	public void migrate(Context context) throws Exception {
		try (Statement stmt = context.getConnection().createStatement()) {
			stmt.execute("PRAGMA foreign_keys = OFF;");
			stmt.execute("CREATE TABLE Players_new (`UUID` varchar(36) PRIMARY KEY, `Killpoints` int NOT NULL, `NationID` int, `Rank` varchar(32) not null, foreign key(NationID) references Nations(NationID) on delete set null);");
			stmt.execute("INSERT INTO Players_new SELECT * FROM Players;");
			stmt.execute("DROP TABLE Players;");
			stmt.execute("ALTER TABLE Players_new RENAME TO Players;");
			stmt.execute("PRAGMA foreign_keys = ON;");
			
			stmt.execute("DROP TABLE IF EXISTS Ranks;");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
