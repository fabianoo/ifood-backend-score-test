package ifood.score.order;

import ifood.score.jms.JmsBridge;
import ifood.score.mock.generator.order.OrderPicker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

import static ifood.score.mock.generator.RandomishPicker._int;

@Service
public class OrderCheckoutMock {

	@Value("${score.order.checkout.queue-name}")
	private String checkoutOrderQueue;

	@Value("${score.order.cancel.queue-name}")
	private String cancelOrderQueue;

	@Autowired
	JmsBridge jmsBridge;
	
	private ConcurrentLinkedQueue<UUID> cancellantionQueue = new ConcurrentLinkedQueue<>();
	
	private static OrderPicker picker = new OrderPicker();

	@Scheduled(fixedRate=3 * 1000)
	public void checkoutFakeOrder(){
		IntStream.rangeClosed(1, _int(2, 12)).forEach(t -> {
			Order order = picker.pick();
			if(_int(0, 20) % 20 == 0){
				cancellantionQueue.add(order.getUuid());
			}
			jmsBridge.sendMessage(checkoutOrderQueue, order);
		});
	}
	
	@Scheduled(fixedRate=30 * 1000)
	public void cancelFakeOrder(){
		IntStream.range(1, _int(2, cancellantionQueue.size() > 2 ? cancellantionQueue.size() : 2)).forEach(t ->{
			UUID orderUuid = cancellantionQueue.poll();
			if(orderUuid != null){
				jmsBridge.sendMessage(cancelOrderQueue, orderUuid);
			}
		});
	}
}
