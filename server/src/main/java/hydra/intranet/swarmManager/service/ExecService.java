package hydra.intranet.swarmManager.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExecService {

	public String exec(final String cmd) throws Exception {

		final Runtime rt = Runtime.getRuntime();
		final Process proc = rt.exec(cmd);

		final BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		final BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		final StringBuffer stdout = new StringBuffer();
		final StringBuffer errout = new StringBuffer();

		// read the output from the command
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			stdout.append(s);
		}

		// read any errors from the attempted command
		while ((s = stdError.readLine()) != null) {
			errout.append(s);
		}

		proc.waitFor();

		if (!StringUtils.isEmpty(errout.toString())) {
			log.debug("Error in command: '{}' Error is: '{}'", cmd, errout.toString());
		}

		return stdInput.toString();

	}

	public void httpGet(final String url) throws Exception {
		final URL obj = new URL(url);
		final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");

		final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		final StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	}

}
