package jadx.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jadx.api.JadxDecompiler;
import jadx.core.utils.exceptions.JadxArgsValidateException;

public class JadxCLI {
	private static final Logger LOG = LoggerFactory.getLogger(JadxCLI.class);

	public static void main(String[] args) {
		try {
			String[] arguments = new String[2];
			arguments[0] = "/home/mike/Dev/School/Research/LogicBombs/test/SmsReminder.apk";//"/home/mike/Dev/School/Research/LogicBombs/test/0c67d0919e574a6876c73118260368ee.apk";
			arguments[1] = "--no-replace-consts";
			
			JadxCLIArgs jadxArgs = new JadxCLIArgs();
			if (jadxArgs.processArgs(arguments)) {
				processAndSave(jadxArgs);
			}
		} catch (Exception e) {
			LOG.error("jadx error: {}", e.getMessage(), e);
			System.exit(1);
		}
	}

	static void processAndSave(JadxCLIArgs inputArgs) {
		JadxDecompiler jadx = new JadxDecompiler(inputArgs.toJadxArgs());
		try {
			jadx.load();
		} catch (JadxArgsValidateException e) {
			LOG.error("Incorrect arguments: {}", e.getMessage());
			System.exit(1);
		}
		jadx.save();
		
		if (jadx.getErrorsCount() != 0) {
			jadx.printErrorsReport();
			LOG.error("finished with errors");
		} else {
			LOG.info("done");
		}
	}
}
