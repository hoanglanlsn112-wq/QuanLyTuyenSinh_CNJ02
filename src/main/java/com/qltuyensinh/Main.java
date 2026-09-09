package com.qltuyensinh;

import javax.swing.SwingUtilities;

import com.qltuyensinh.util.UITheme;
import com.qltuyensinh.view.LoginForm;

public class Main {
    public static void main(String[] args) {
        UITheme.applyLookAndFeel();
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}