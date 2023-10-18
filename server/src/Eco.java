import java.util.HashMap;

public class Eco
{
	private final String id;
	private final HashMap<String, String> data = new HashMap<>();

	public Eco(String id) { this.id = id; }

	public void save_persistent() { }
}
