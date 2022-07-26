package Emp_Connector.SFDC_Events;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;
import org.eclipse.jetty.util.ajax.JSON;

import static org.cometd.bayeux.Channel.*;


public class DevLoginExample {

	private final ExecutorService workerThreadPool = Executors.newFixedThreadPool(1);
	List<String> lc;

	public static void main(String[] argv) throws Throwable {
		DevLoginExample devLoginExample = new DevLoginExample();
		devLoginExample.processEvents();
	}

	public void processEvents() throws Throwable {

		lc = ServiceCredential.loginProperties();
		BearerTokenProvider tokenProvider = new BearerTokenProvider(() -> {
			try {
				return LoginHelper.login(new URL(lc.get(0)),lc.get(1),lc.get(2));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		BayeuxParameters params = tokenProvider.login();

		EmpConnector connector = new EmpConnector(params);
		LoggingListener loggingListener = new LoggingListener(true, true);

		connector.addListener(META_HANDSHAKE, loggingListener).addListener(META_CONNECT, loggingListener)
				.addListener(META_DISCONNECT, loggingListener).addListener(META_SUBSCRIBE, loggingListener)
				.addListener(META_UNSUBSCRIBE, loggingListener);

		connector.setBearerTokenProvider(tokenProvider);

		connector.start().get(5, TimeUnit.SECONDS);

		long replayFrom = EmpConnector.REPLAY_FROM_TIP;
		if (!lc.get(4).isEmpty()) {
			replayFrom = Long.parseLong(lc.get(4));
		}

		try {
			DatabaseService service = new DatabaseService();
	        Consumer<Map<String, Object>> consumer = event -> workerThreadPool.submit(() -> 
	        System.out.println(String.format("Received:\n%s", JSON.toString(event),Thread.currentThread().getName(), Thread.currentThread().getId())));

	        consumer=event->event.forEach((k,v) -> service.dataProcess(k,v));
	        TopicSubscription subscription = connector.subscribe(lc.get(3), replayFrom, consumer).get(5, TimeUnit.SECONDS);

		} catch (ExecutionException e) {
			System.err.println(e.getCause().toString());
			System.exit(1);
			throw e.getCause();
		} catch (TimeoutException e) {
			System.err.println("Timed out subscribing");
			System.exit(1);
			throw e.getCause();
		}
	}

}
