import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CheckNonRepudiation {
    public static List<List<String>> readReceipt(String file) {
        List<List<String>> res = new ArrayList<>();
        String fileToParse = file;
        BufferedReader fileReader = null;
        final String DELIMITER = ",";
        try {
            String line = "";
            fileReader = new BufferedReader(new FileReader(fileToParse));
            while ((line = fileReader.readLine()) != null) {
                List<String> tmp = new ArrayList<>();
                String[] tokens = line.split(DELIMITER);
                for(int i = 0; i < tokens.length - 1; i++)
                    tmp.add(tokens[i]);
                res.add(tmp);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                fileReader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
    public static void checkNonRepudiation(Cipher c, PublicKey pub_client){
        try {
            List<List<String>> res = readReceipt("F:\\NS Project\\id.time.amount.transactionID.receipt.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Verifying client's purchase.");
            System.out.print("Input time (format is HH:mm:ss): ");
            String time = br.readLine();
            System.out.print("Input transactionID: ");
            String rr = br.readLine();

            List<String> pick = new ArrayList<>();
            for (List<String> s : res){
                if (s.get(1).equals(time) && s.get(3).equals(rr))
                    pick = new ArrayList<>(s);
            }

            if (pick.size() != 0){
                String receipt = pick.get(4);
                byte[] decrypt = Base64.getDecoder().decode(receipt);
                c = Cipher.getInstance("RSA");
                c.init(Cipher.DECRYPT_MODE, pub_client);
                decrypt = c.doFinal(decrypt);
                String[] token = new String(decrypt).split(",");
                System.out.println("Client owes " + token[0] + " of money.");
            }
            else {
                System.out.println("No such record.");
            }
        }
        catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e){
        }
    }
}
