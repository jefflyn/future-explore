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

    public static void createMsgFrame(String code, Boolean isUp, String content) {
        try {
            String msg = (isUp ? "up " : "down ") + code;
//            Runtime.getRuntime().exec("say " + msg);
            if (frame == null) {
                frame = new JFrame("price flash");
                frame.setLayout(new FlowLayout());
                frame.setBounds(0, 1000, 500, 120);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        frame.dispose();
                        frame.getContentPane().removeAll();
                    }
                });
                frame.setVisible(true);
            }
            JLabel jl = new JLabel(content);
            Container c = frame.getContentPane();
            c.add(jl, 0);
            if (c.getComponentCount() > 4) {
                for (int i = 5; i < c.getComponentCount(); i++) {
                    c.remove(i);
                }
            }
//            frame.setExtendedState(JFrame.ICONIFIED);
        } catch (Exception e) {
            log.error("create price flash msg frame failed, error={}", e);
        }
    }

    public static void main(String[] args) throws IOException {
        WindowUtil.createMsgFrame("A2201", true, "50秒 上涨0.36%【823.0-826.0】看多");
    }
}
