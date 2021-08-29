import burp.KeyValuesDialog;

public class TestKeyValues {
    public static void main(String[] args) {
        KeyValuesDialog keyValuesDialog = new KeyValuesDialog();

        for (int i = 0; i < 10; i++) {
            keyValuesDialog.addRow(new Object[]{"key_" + i, "value_" + i, true});
        }

        keyValuesDialog.pack();


        keyValuesDialog.setDefaultCloseOperation(KeyValuesDialog.DISPOSE_ON_CLOSE);
        keyValuesDialog.setVisible(true);

//        List keys = keyValuesDialog.getSelectKeys();
//
//        for (int i = 0; i < keys.size(); i++) {
//            System.out.println(keys.get(i));
//        }

        System.exit(0);

    }
}
