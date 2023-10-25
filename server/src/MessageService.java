import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListMap;

public final class MessageService
{
	public static ConcurrentSkipListMap<ZonedDateTime, Message> chat = new ConcurrentSkipListMap<>(Comparator.comparingLong(v -> v.toInstant().toEpochMilli()));

	public static void append_message(ZonedDateTime time, String echo_id, String message)
	{
		Message message_instance = new Message(echo_id, message);
		chat.put(time, message_instance);
	}

	public static ConcurrentSkipListMap<ZonedDateTime, Message> get_full_chat()
	{
		return chat;
	}
}

record Message(String echo_id, String content) {}