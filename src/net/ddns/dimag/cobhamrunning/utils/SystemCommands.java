package net.ddns.dimag.cobhamrunning.utils;

public interface SystemCommands {
	String uptimeCmd = "awk '{print $1}' /proc/uptime";
	String measEnvCmd = "measurements dump env --json";
	String swvCmd = "axsh get swv";

	String IPADDRESS_PATTERN_INNER = "(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d))";
	String IPADDRESS_PATTERN = String.format("^%s$", IPADDRESS_PATTERN_INNER);
	String NETWORK_PATTERN = "(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}";

	String MACADDRESS_PATTERN = "^([0-9a-fA-F]{2}[:.-]?){5}[0-9a-fA-F]{2}$";
	String MACADDRESS_PATTERN_INNER = "([0-9a-fA-F]{2}[:.-]?){5}[0-9a-fA-F]{2}";

	String BETWEEN_PATTERN = "(?<=%s)(.*?)(?=%s)";

	String swvTemplateCmd = "^XA\n" +
			"^LH20,3\n" +
			"^FO400,40^CF0,60^FD%s^FS\n" +
			"^FO5,15^A0N,30,30^FDTarget: ^FS\n" +
			"^FO120,15^A0N,30,30^FD%s^FS\n" +
			"^FO5,50^A0N,30,30^FDCommon: ^FS\n" +
			"^FO120,50^A0N,30,30^FD%s^FS\n" +
			"^FO5,85^A0N,30,30^FDSystem:^FS\n" +
			"^FO120,85^A0N,30,30^FD%s^FS\n" +
			"^XZ";

	String rmvTemplateCmd = "^XA\n" +
			"^LH20,3\n" +
			"^FO5,15^A0N,30,30^FDSystem:^FS\n" +
			"^FO135,15^A0N,30,30^FD%s^FS\n" +
			"^FO5,50^A0N,30,30^FDTest:^FS\n" +
			"^FO135,50^A0N,30,30^FD%s %s^FS\n" +
			"^FO5,85^A0N,30,30^FDRMV date:^FS\n" +
			"^FO135,85^A0N,30,30^FD%s^FS\n" +
			"^XZ";

	String envDeviceTemplate = "^XA\n" +
			"^LH20,3\n" +
			"^FO5,15^CF0,35^FDService: ^FS\n" +
			"^FO150,15^CF0,35^FD%s^FS\n" +
			"^FO5,55^CF0,35^FDModel: ^FS\n" +
			"^FO150,55^CF0,35^FD%s^FS\n" +
			"^FO5,95^CF0,35^FDSerial: ^FS\n" +
			"^FO150,95^CF0,35^FD%s^FS\n" +
			"^FO470,6^BQN,2,3^FDQA,%s^FS\n" +
			"^XZ";

	String innerDeviceTemplate = "^XA\n" +
			"^LH20,3\n" +
			"^FO0,40^AGN,55,25^FD<asis>^FS\n" +
			"^FO200,20^AFN,20,13^FD<article>^FS\n" +
			"^FO200,50^AFN,20,13^FD<descript>^FS\n" +
			"^FO200,80^AFN,20,13^FD<date>^FS\n" +
			"^FO450,95^A0N,20,13^FDFor internal use only!^FS\n" +
			"^FO470,6^BQN,2,3^FDQA,<article>/<asis>^FS\n" +
			"^XZ";

}
