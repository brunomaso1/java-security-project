/*
 * Universidad Catolica - Seguridad - Obligatorio.
 */
package implementationCI;

import java.io.FileInputStream; 
import java.io.IOException;
import java.io.InputStream; 
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Properties; 
import java.util.

/**
 * Esta clase es la encargada de interactuar con la Cedula de Identidad.
 * @author Masoller, Artegoytia, Galleto, Olivera.
 */
public class SecImp implements ISecImp {
    private String configName;
    private Provider provider;
    private X509Certificate certificado;

    public void SecImp(String configName) { this.configName = configName; }

    public void SecImp() {
        String configName = ".\\conf\\pkcs11.cfg";
        Provider provider = new sun.security.pkcs11.SunPKCS11(configName);
        Security.addProvider(provider);

        // Log
        System.out.println("Se ha inicializado el provider correctamente.");
    }

    public void remProvider() { 
        Security.removeProvider(provider.getName());

        // Log
        System.out.println("Se ha removido el provider correctamente.");
    }

    public void conectCI() {
        String pin = "";
        KeyStore cc = KeyStore.getInstance("PKCS11", provider);
        KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(pin.toCharArray());
        cc.load(null,  pp.getPassword());
        Enumeration aliases = cc.aliases();
        while (aliases.hasMoreElements()) {
            Object alias = aliases.nextElement();
            try {
                certificado = (X509Certificate) cc.getCertificate(alias.toString());

                // Log
                System.out.println("Se ha obtenido el certificado correctamente.");

                System.out.println("I am: " + cert0.getSubjectDN().getName());
                break;
            } catch (Exception e) {
                continue;
            }
        }
    }

    public String getName() {
        if certificado != null
            
    }

    public static void main(String[] args) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        
        //String pin = "Poner pin aca"; 
        //KeyStore ks = KeyStore.getInstance("PKCS11");
        //ks.load(null, pin.toCharArray());
        //ks.load(null, null);
                
        
    }
    
}
