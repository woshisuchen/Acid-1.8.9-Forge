package acid.api;

public class AALAPI
{
    private static String username;
    
    public static String getUsername() {
        if (AALAPI.username == null) {
            AALAPI.username = getUsernameUncached();
        }
        return AALAPI.username;
    }
    
    private static String getUsernameUncached() {
        Class<?> api;
        try {
            api = Class.forName("net.aal.API");
        }
        catch (ClassNotFoundException ignored) {
            api = null;
        }
        if (api == null) {
            return "debug-mode";
        }
        try {
            return (String)api.getMethod("getUsername", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "error-getting-username";
        }
    }
}
