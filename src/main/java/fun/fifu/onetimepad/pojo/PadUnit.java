package fun.fifu.onetimepad.pojo;

import lombok.Data;

@Data
public class PadUnit {
    String encryptionType;
    String plaintext;
    byte[][] keyGroup;
    String keyGroupText;
}