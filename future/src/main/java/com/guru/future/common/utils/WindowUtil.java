package com.guru.future.common.utils;

import com.guru.future.domain.OpenGapDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * @author j
 */
@Slf4j
public class WindowUtil {
    private static JFrame frame = null;

    private static DefaultListModel model = new DefaultListModel<>();

    public static void createMsgFrame(String code, Boolean isUp, String content) {
        try {
//          String msg = (isUp ? "up " : "down ") + code;
//          Runtime.getRuntime().exec("say " + msg);
            JLabel label = new JLabel();
            label.setText(content);
            if (model.size() > 30) {
                Object lastOne = model.lastElement();
                model.removeElement(lastOne);
            }
            model.add(0, label);

            if (frame == null) {
                frame = new JFrame("trade log");
                frame.setLayout(new FlowLayout());
                frame.setBounds(0, 1000, 510, 160);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        frame.dispose();
                        frame.getContentPane().removeAll();
                    }
                });
                refreshContentPane();
                frame.setVisible(true);
            } else {
                refreshContentPane();
            }
            frame.validate();
        } catch (Exception e) {
            log.error("create trade log frame failed, error={}", e);
        }
    }

    private static void refreshContentPane(){
        JList list = new JList(model);
        list.setCellRenderer(new MyListCellRenderer());
        JScrollPane pane = new JScrollPane(list);
        pane.setPreferredSize(new Dimension(500, 120));
        Container c = frame.getContentPane();
        c.removeAll();
        c.add(pane, 0);
    }

    public static class MyListCellRenderer extends JLabel implements ListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            // 设置JLable的文字
            JLabel text = (JLabel) value;
            setText(text.getText());//设置JLable的文字
            if (text.getText().contains("多")) {
                setForeground(Color.RED);
            } else {
                setForeground(Color.BLACK);
            }
            return this;
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            WindowUtil.createMsgFrame("A2201", true, i + ": 50秒 上涨0.36%【823.0-826.0】看多");
        }
    }
}
