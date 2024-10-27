package dstpsend;
import java.security.Provider;
import java.security.Security;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;




public interface ICheckCFG {

    public boolean validateConfig(Map<String, String> config);
}


class CheckCFG  implements ICheckCFG {

    public CheckCFG() {
        super();
    }

    
    @Override
    public boolean validateConfig(Map<String, String> config) {

        Provider[] providers = Security.getProviders();
        Set<String> ciphers = new HashSet<String>();
        Set<String> macs = new HashSet<String>();
        Set<String> messageDigests = new HashSet<String>();
        
        for (int i = 0; i != providers.length; i++)
        {
            Iterator<?> it = providers[i].keySet().iterator();
            while (it.hasNext())
            {
                String	entry = (String)it.next();

                if (entry.startsWith("Alg.Alias."))
                {
                    entry = entry.substring("Alg.Alias.".length());
                }
                
                if (entry.startsWith("Cipher."))
                {
                    ciphers.add(entry.substring("Cipher.".length()).split(" ")[0]);
                }
                else if (entry.startsWith("MessageDigest."))
                {
                    messageDigests.add(entry.substring("MessageDigest.".length()).split(" ")[0]);
                }
                else if (entry.startsWith("Mac."))
                {
                    macs.add(entry.substring("Mac.".length()).split(" ")[0]);
                }
            }
        }

        System.out.println(config.get("CONFIDENTIALITY"));
        System.out.println(config.get("H"));
        System.out.println(config.get("MAC"));

        if(!ciphers.contains(config.get("CONFIDENTIALITY"))){
            System.out.println("Failed Confidentiality check!");
            return false;
        }

        if(!messageDigests.contains(config.get("H"))){
            System.out.println("Failed Hash check!");
            return false;
        }

        if(!macs.contains(config.get("MAC"))){
            System.out.println("Failed Mac check!");
            return false;
        }

        return true;
    }
}