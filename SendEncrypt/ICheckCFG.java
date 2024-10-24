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
        Set<String>        ciphers = new HashSet<String>();
        Set<String>        keyAgreements = new HashSet<String>();
        Set<String>        macs = new HashSet<String>();
        Set<String>        messageDigests = new HashSet<String>();
        Set<String>        signatures = new HashSet<String>();
        
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
                    ciphers.add(entry.substring("Cipher.".length()));
                }
                else if (entry.startsWith("KeyAgreement."))
                {
                    keyAgreements.add(entry.substring("KeyAgreement.".length()));
                }
                else if (entry.startsWith("Mac."))
                {
                    macs.add(entry.substring("Mac.".length()));
                }
                else if (entry.startsWith("MessageDigest."))
                {
                    messageDigests.add(entry.substring("MessageDigest.".length()));
                }
                else if (entry.startsWith("Signature."))
                {
                    signatures.add(entry.substring("Signature.".length()));
                }
            }
        }
        
        printSet("Ciphers (Alg.): ", ciphers);
        printSet("KeyAgreeents: ", keyAgreements);
        printSet("MACs: ", macs);
        printSet("MessageDigests (Secure Hash-Functions): ", messageDigests);
        printSet("Signatures (Digital Sigantures w/ PubKey Methods) :", signatures);

        return false;
    }

    private static void printSet(
        String setName,
        Set<String> algorithms)
    {
        System.out.println("----------------------------------------------------");
        System.out.println(setName);
        System.out.println("----------------------------------------------------");
        
        if (algorithms.isEmpty())
        {
	    System.out.println("------------------------------------------------");
            System.out.println("            None available.");
	    System.out.println("------------------------------------------------");
        }
        else
        {
            Iterator<?>	it = algorithms.iterator();
            
            while (it.hasNext())
            {
                String	name = (String)it.next();
                
                System.out.println("  " + name);
            }
        }
    }
}