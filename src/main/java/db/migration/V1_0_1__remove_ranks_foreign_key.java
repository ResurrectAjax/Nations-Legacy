package db.migration;

import java.sql.ResultSet;
import java.sql.Statement;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V1_0_1__remove_ranks_foreign_key extends BaseJavaMigration{

	@Override
	public void migrate(Context context) throws Exception {
		try (Statement stmt = context.getConnection().createStatement()) {
			
			ResultSet columns = context.getConnection().getMetaData().getColumns(null, null, "Ranks", "Power");
			if(!columns.next()) stmt.execute("ALTER TABLE Ranks ADD Power int");
			
			stmt.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
