package burp;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum TABLE_SELECT_MODELS {
    SIG, MUL, MIX
}

public class UsersViewsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel container;
    private JTable usersTable;
    private JTable kvsTable;
    private JTextField roleTextField;
    private JTextField nameTextField;
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JPanel editorComposeContainerPane;
    private JLabel roleLabel;
    private JLabel nameLabel;
    private JPanel editorUserTablePane;
    private JScrollPane editorUserTableScrollPane;
    private JPanel usersViewContainerPane;
    private JPanel kvsContainerPane;
    private JScrollPane kvsTableScrollPane;
    private JPanel finaOptionContainerPane;
    private JPanel oprationComposeContainerPane;
    private JPanel KVsTableOptsBtnPane;
    private JButton selectAllButton;
    private JButton selectNoneButton;
    private Map<String, String> TmpkeyValuesForUpdate;

    private DefaultTableModel userModel;
    private DefaultTableModel kvsModel;
    private boolean addedFlag = false;

//    private List<Color> rowBackgroundColors;
//    private List<Color> rowForegroundColors;

    public UsersViewsDialog() {
        $$$setupUI$$$();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!addedFlag) {
                    addUserDefault();
                    addedFlag = true;
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String role = roleTextField.getText().trim();
                String name = nameTextField.getText().trim();
                Map<String, String> keyValues = getTmpkeyValuesForUpdate();

                USER_OPT_STATUS user_opt_status = AuthInfosDB.updateAuthInfo(role, name, keyValues);
                if (USER_OPT_STATUS.UPDATE_USER_SUCCESS == user_opt_status) {
                    JOptionPane.showMessageDialog(null, "update success!", "success", JOptionPane.INFORMATION_MESSAGE);
                    userModel.setValueAt(role, 0, usersTable.getSelectedRow());
                    userModel.setValueAt(name, 1, usersTable.getSelectedRow());

                    keyValues = AuthInfosDB.getSessionInfoByRoleAndName(role, name);
                    if (null == keyValues) {
                        return;
                    }
                    kvsModel.getDataVector().removeAllElements();
                    kvsModel.fireTableDataChanged();
                    addKVsByUserSessionInfos(keyValues);
                    kvsModel.fireTableDataChanged();
////                    kvsTable.updateUI();


                    deleteButton.setEnabled(true);

                } else {
                    JOptionPane.showMessageDialog(null, "update failed! user not existed!", "failed", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String role = roleTextField.getText().trim();
                String name = nameTextField.getText().trim();

                USER_OPT_STATUS user_opt_status = AuthInfosDB.deleteAuthInfo(role, name);
                if (USER_OPT_STATUS.DELETE_USER_SUCCESS == user_opt_status) {
                    JOptionPane.showMessageDialog(null, "delete success!", "success", JOptionPane.INFORMATION_MESSAGE);
                    userModel.removeRow(usersTable.getSelectedRow());

                    updateButton.setEnabled(true);

                } else {
                    JOptionPane.showMessageDialog(null, "delete failed! user not existed!", "failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        usersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int rowNum = usersTable.getSelectedRow();
                if (-1 != rowNum) {
                    String role = (String) userModel.getValueAt(rowNum, 0);
                    String name = (String) userModel.getValueAt(rowNum, 1);

                    roleTextField.setText(role);
                    nameTextField.setText(name);

                    Map<String, String> keyValues = AuthInfosDB.getSessionInfoByRoleAndName(role, name);
                    if (null == keyValues) {
                        return;
                    }

                    kvsModel.getDataVector().removeAllElements();
                    kvsModel.fireTableDataChanged();
                    addKVsByUserSessionInfos(keyValues);
                    kvsModel.fireTableDataChanged();
                }
            }
        });
        selectAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cnt = kvsModel.getRowCount();
                for (int i = 0; i < cnt; i++) {
                    kvsModel.setValueAt(true, i, 2);
                }
            }
        });
        selectNoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cnt = kvsModel.getRowCount();
                for (int i = 0; i < cnt; i++) {
                    kvsModel.setValueAt(false, i, 2);
                }
            }
        });
    }

    private void onOK() {
        // add your code here
        if (AuthInfosDB.getUser_view_dialog_mode() == USER_VIEW_DIALOG_MODELS.ADD && !addedFlag) {
            addUserDefault();
            addedFlag = true;
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        userModel = new DefaultTableModel();
        userModel.setColumnIdentifiers(new Object[]{"role", "name", "currentUser"});

        usersTable = new JTable(userModel) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 1:
                    case 2:
                        return String.class;
                    default:
                        return boolean.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                switch (AuthInfosDB.getUser_view_dialog_mode()) {
                    case ADD:
                    case REPLACE:
                    case DELTE:
                        return false;
                    default:
                        if (2 == column) {
                            return false;
                        }
                        return true;
                }

            }
        };


        kvsModel = new DefaultTableModel();
        kvsModel.setColumnIdentifiers(new Object[]{"key", "value", "selected"});

        kvsTable = new JTable(kvsModel) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    default:
                        return Boolean.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return 2 <= column;
            }
        };

        TmpkeyValuesForUpdate = new HashMap<>();
    }

    public void addUserRow(Object[] rowData) {
        userModel.addRow(rowData);
    }

    public void addKVSRow(Object[] rowData) {
        kvsModel.addRow(rowData);
    }

    public void addKVsRowsByHeaders(List<String> headers) {
        kvsModel.getDataVector().clear();
        for (String header : headers) {
            String[] keyValue = header.split(": ");
            if (keyValue.length <= 1) {
                continue;
            }
            String key = keyValue[0];
            String value = keyValue[1];

            kvsModel.addRow(new Object[]{key, value, true});

        }
    }

    public void addKVsByUserSessionInfos(Map<String, String> userSessionInfos) {
        kvsModel.getDataVector().clear();
        for (Map.Entry<String, String> enttry : userSessionInfos.entrySet()) {
            kvsModel.addRow(new Object[]{enttry.getKey(), enttry.getValue(), true});
        }
    }

    public void initData(List<String> headers) {
        addKVsRowsByHeaders(headers);
        roleTextField.setText("role_" + AuthInfosDB.getUserIndex());
        nameTextField.setText("name_" + AuthInfosDB.getUserIndex());
    }

    public Map<String, String> getSelectKeyValues() {
        int cnt = kvsModel.getRowCount();
        Map<String, String> keyValues = new HashMap<>();

        for (int i = 0; i < cnt; i++) {
            if ((boolean) kvsModel.getValueAt(i, 2)) {
                keyValues.put((String) kvsModel.getValueAt(i, 0), (String) kvsModel.getValueAt(i, 1));
            }
        }
        return keyValues;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        oprationComposeContainerPane = new JPanel();
        oprationComposeContainerPane.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(oprationComposeContainerPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        oprationComposeContainerPane.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        finaOptionContainerPane = new JPanel();
        finaOptionContainerPane.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        oprationComposeContainerPane.add(finaOptionContainerPane, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        finaOptionContainerPane.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        finaOptionContainerPane.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        container = new JPanel();
        container.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(container, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        usersViewContainerPane = new JPanel();
        usersViewContainerPane.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        container.add(usersViewContainerPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        editorComposeContainerPane = new JPanel();
        editorComposeContainerPane.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        usersViewContainerPane.add(editorComposeContainerPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        roleLabel = new JLabel();
        roleLabel.setText("role：");
        editorComposeContainerPane.add(roleLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        roleTextField = new JTextField();
        editorComposeContainerPane.add(roleTextField, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        nameTextField = new JTextField();
        editorComposeContainerPane.add(nameTextField, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        addButton = new JButton();
        addButton.setText("add");
        editorComposeContainerPane.add(addButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteButton = new JButton();
        deleteButton.setText("delete");
        editorComposeContainerPane.add(deleteButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        updateButton = new JButton();
        updateButton.setText("update");
        editorComposeContainerPane.add(updateButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nameLabel = new JLabel();
        nameLabel.setText("name：");
        editorComposeContainerPane.add(nameLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editorUserTablePane = new JPanel();
        editorUserTablePane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        usersViewContainerPane.add(editorUserTablePane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        editorUserTableScrollPane = new JScrollPane();
        editorUserTablePane.add(editorUserTableScrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        editorUserTableScrollPane.setViewportView(usersTable);
        kvsContainerPane = new JPanel();
        kvsContainerPane.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        container.add(kvsContainerPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        kvsTableScrollPane = new JScrollPane();
        kvsContainerPane.add(kvsTableScrollPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        kvsTableScrollPane.setViewportView(kvsTable);
        KVsTableOptsBtnPane = new JPanel();
        KVsTableOptsBtnPane.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        kvsContainerPane.add(KVsTableOptsBtnPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectAllButton = new JButton();
        selectAllButton.setText("selectAll");
        KVsTableOptsBtnPane.add(selectAllButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        KVsTableOptsBtnPane.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        selectNoneButton = new JButton();
        selectNoneButton.setText("selectNone");
        KVsTableOptsBtnPane.add(selectNoneButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        roleLabel.setLabelFor(roleTextField);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    public boolean setUsersTableSelectRow(int row) {
        if (0 > row || row > userModel.getRowCount()) {
            return false;
        }
        usersTable.isRowSelected(row);
        return true;
    }

    public Map<String, String> getTmpkeyValuesForUpdate() {
        return TmpkeyValuesForUpdate;
    }

    public void setTmpkeyValuesForUpdate(Map<String, String> tmpkeyValuesForUpdate) {
        TmpkeyValuesForUpdate = tmpkeyValuesForUpdate;
    }

    public void setUserstableSelecttionModel(TABLE_SELECT_MODELS table_select_models) {
        switch (table_select_models) {
            case SIG:
                usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                break;
            case MUL:
                usersTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                break;
            default:
                usersTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                break;
        }
    }

    public void setReplaceUserDialogConfig(List<String> headers) {
        usersTable.setEnabled(true);
//        usersTable.setEditingRow();

        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
        updateButton.setEnabled(false);

        AuthInfo authInfo = AuthInfosDB.findAuthInfoByHeaders(headers);
        if (null == authInfo) {
            return;
        }
        int userRowCnt = userModel.getRowCount();

        for (int i = 0; i < userRowCnt; i++) {
            String role = (String) userModel.getValueAt(i, 0);
            String name = (String) userModel.getValueAt(i, 1);

            if (role.equals(authInfo.getRoleName()) && name.equals(authInfo.getUserName())) {
                setUsersTableSelectRow(i);
                userModel.setValueAt(true, i, 2);
            }
        }


    }

    public void setAddUserDialogConfig() {
        usersTable.setEnabled(false);
        addButton.setEnabled(true);
        deleteButton.setEnabled(false);
        updateButton.setEnabled(false);
    }

    public void setUpdateUserDialogConfig() {
        usersTable.setEnabled(true);
        setUserstableSelecttionModel(TABLE_SELECT_MODELS.SIG);

        updateButton.setEnabled(true);
        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    public void setDeleteUserDialogConfig() {
        usersTable.setEnabled(true);

        deleteButton.setEnabled(true);
        addButton.setEnabled(false);
        updateButton.setEnabled(false);
    }

    public Map<String, String> getSelectReplaceUserSessionInfo() {

        int selectedUserRow = usersTable.getSelectedRow();
        String role = (String) userModel.getValueAt(selectedUserRow, 0);
        String name = (String) userModel.getValueAt(selectedUserRow, 1);

        return AuthInfosDB.getSessionInfoByRoleAndName(role, name);
    }

    private void addUserDefault() {
        String role = roleTextField.getText().trim();
        String name = nameTextField.getText().trim();

        if (role.isEmpty()) {
            JOptionPane.showMessageDialog(null, "role field is empty!", "warring", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "name field is empty!", "failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, String> keyValues = getSelectKeyValues();
        AuthInfo authInfo = new AuthInfo(role, name, keyValues);

        USER_OPT_STATUS status = AuthInfosDB.addAuthInfo(authInfo);
        BurpExtender.StdoutPrintln("addAuthInfo: " + status);
        if (USER_OPT_STATUS.ADD_USER_SUCCESS == status) {
            AuthInfosDB.setUserIndex(AuthInfosDB.getUserIndex() + 1);
            JOptionPane.showMessageDialog(null, "add success!", "success", JOptionPane.INFORMATION_MESSAGE);
            addUserRow(new Object[]{role, name, false});
            usersTable.setEnabled(true);
            addButton.setEnabled(false);
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
//            addedFlag = true;

        } else {
            JOptionPane.showMessageDialog(null, "add failed! user existed!", "failed", JOptionPane.ERROR_MESSAGE);
        }
    }

}
