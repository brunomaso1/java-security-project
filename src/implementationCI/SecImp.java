/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementationCI;

import java.io.FileInputStream; 
import java.io.IOException;
import java.io.InputStream; 
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Properties; 

/**
 *
 * @author bruno
 */
public class SecImp {

    /**
     * @param args the command line arguments
     * @throws java.security.KeyStoreException
     * @throws java.io.IOException
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.cert.CertificateException
     */
    public static void main(String[] args) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        String configName = ".\\conf\\pkcs11.cfg";
        Provider p = new sun.security.pkcs11.SunPKCS11(configName);
        Security.addProvider(p);
        
        //String pin = "Poner pin aca"; 
        //KeyStore ks = KeyStore.getInstance("PKCS11");
        //ks.load(null, pin.toCharArray());
        //ks.load(null, null);
                
        String pkcs11ProviderName = p.getName();
        Security.removeProvider(pkcs11ProviderName);
    }
    
}
