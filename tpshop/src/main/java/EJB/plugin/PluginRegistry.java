package EJB.plugin; 
import jakarta.ejb.Singleton; 
import jakarta.ejb.Startup; 
import jakarta.annotation.PostConstruct; 
import java.util.ArrayList; 
import java.util.List; 
@Singleton 
@Startup 
public class PluginRegistry { 
private List<CartPlugin> plugins = new ArrayList<>(); 
@PostConstruct 
public void init() { 
    plugins.add(new DiscountPlugin()); 
    plugins.add(new SuggestionPlugin()); 
    plugins.add(new LoyaltyPlugin()); 
} 
public List<CartPlugin> getPlugins() { return plugins; } 
} 