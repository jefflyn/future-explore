package com.guru.future.common.utils;


import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Date;

/**
 * @author j
 */
@Slf4j
public class WindowUtil {
    private static JFrame frame = null;

    public static void createMsgFrame(String code, String msg) {
        try {
            Runtime.getRuntime().exec("say price flash " + code);
            if (frame == null) {
                frame = new JFrame("price flash");
                frame.setLayout(new FlowLayout());
                frame.setBounds(0, 1000, 420, 120);
//                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        frame.dispose();
                        frame.getContentPane().removeAll();
                    }
                });
            }
            JLabel jl = new JLabel(msg);
            Container c = frame.getContentPane();
            c.add(jl, 0);
            frame.setVisible(true);
            if (c.getComponentCount() > 4) {
                for (int i = 5; i < c.getComponentCount(); i++) {
                    c.remove(i);
                }
            }
            frame.setExtendedState(JFrame.ICONIFIED);
        } catch (Exception e) {
            log.error("create price flash msg frame failed, error={}", e);
        }
    }

    public static void main(String[] args) throws IOException {
        WindowUtil.createMsgFrame("A2201", "50秒 上涨0.36%【823.0-826.0】看多");
    }
}
