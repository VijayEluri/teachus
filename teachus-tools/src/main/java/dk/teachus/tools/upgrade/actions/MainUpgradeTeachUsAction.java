package dk.teachus.tools.upgrade.actions;

import java.io.File;

import dk.teachus.tools.upgrade.config.MainDeploymentNode;
import dk.teachus.tools.upgrade.config.MavenNode;
import dk.teachus.tools.upgrade.config.SubversionReleaseNode;
import dk.teachus.tools.upgrade.config.TomcatNode;

public class MainUpgradeTeachUsAction extends UpgradeTeachUsAction {

	public MainUpgradeTeachUsAction(MavenNode maven, SubversionReleaseNode subversion, File workingDirectory, MainDeploymentNode deployment, TomcatNode tomcat, String version) throws Exception {
		super(maven, subversion, workingDirectory, deployment, tomcat, version);
	}
	
	@Override
	protected String getName() {
		return "main";
	}
	
	@Override
	protected void beforeUpgradeDatabase() throws Exception {
		File backupFile = new File(workingDirectory, "backup.sql");
		
		BackupDatabaseAction backupDatabase = new BackupDatabaseAction(tomcat.getHost(), deployment.getDatabase(), backupFile);
		backupDatabase.execute();
	}

}
