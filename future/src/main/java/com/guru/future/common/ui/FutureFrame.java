package com.guru.future.common.ui;

import com.google.common.base.Strings;
import com.guru.future.common.cache.LiveDataCache;
import com.guru.future.common.entity.vo.FutureLiveVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author j
 */
@Slf4j
public class FutureFrame {
    private static FutureFrame futureFrame;
    private static String overview;

    private JFrame frame;
    private DefaultListModel<JLabel> changeLogModel = new DefaultListModel<>();
    private DefaultListModel<JLabel> positionLogModel = new DefaultListModel<>();
    private DefaultListModel<JLabel> highTopModel = new DefaultListModel<>();
    private DefaultListModel<JLabel> lowTopModel = new DefaultListModel<>();
    private DefaultListModel<JLabel> changeHighTopModel = new DefaultListModel<>();
    private DefaultListModel<JLabel> changeLowTopModel = new DefaultListModel<>();

    private ReentrantLock lock = new ReentrantLock();

    private FutureFrame() {
    }

    public static synchronized FutureFrame buildFutureFrame(String overviewStr) {
        if (!Strings.isNullOrEmpty(overviewStr)) {
            overview = overviewStr;
        }
        if (futureFrame == null) {
            futureFrame = new FutureFrame();
        }
        return futureFrame;
    }

    private void buildTopModel() {
        highTopModel = new DefaultListModel<>();
        lowTopModel = new DefaultListModel<>();
        for (int i = 0; i < LiveDataCache.getPositionHighTop10().size(); i++) {
            FutureLiveVO highTop = LiveDataCache.getPositionHighTop10().get(i);
            JLabel label = new JLabel();
            label.setText(highTop.toString());
            highTopModel.add(i, label);
        }
        for (int i = 0; i < LiveDataCache.getPositionLowTop10().size(); i++) {
            FutureLiveVO lowTop = LiveDataCache.getPositionLowTop10().get(i);
            JLabel label = new JLabel();
            label.setText(lowTop.toString());
            lowTopModel.add(i, label);
        }
        changeHighTopModel = new DefaultListModel<>();
        changeLowTopModel = new DefaultListModel<>();
        for (int i = 0; i < LiveDataCache.getChangeHighTop10().size(); i++) {
            FutureLiveVO highTop = LiveDataCache.getChangeHighTop10().get(i);
            JLabel label = new JLabel();
            label.setText(highTop.toString());
            changeHighTopModel.add(i, label);
        }
        for (int i = 0; i < LiveDataCache.getChangeLowTop10().size(); i++) {
            FutureLiveVO lowTop = LiveDataCache.getChangeLowTop10().get(i);
            JLabel label = new JLabel();
            label.setText(lowTop.toString());
            changeLowTopModel.add(i, label);
        }
    }

    private void setLogDataModel(DefaultListModel<JLabel> logDataModel, String content) {
        JLabel label = new JLabel();
        label.setText(content);
        if (logDataModel.size() > 30) {
            Object lastOne = logDataModel.lastElement();
            logDataModel.removeElement(lastOne);
        }
        logDataModel.add(0, label);
    }

    public void createMsgFrame(String content) {
        try {
            lock.lock();
            if (StringUtils.isNoneBlank(content)) {
                if (content.contains("上涨") || content.contains("下跌")) {
                    setLogDataModel(changeLogModel, content);
                } else {
                    setLogDataModel(positionLogModel, content);
                }
            }
            buildTopModel();
            if (frame == null) {
                frame = new JFrame();
                frame.setLayout(new FlowLayout());
                frame.setBounds(0, 1000, 530, 430);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        frame.dispose();
                    }
                });
                JTabbedPane tabbedPane = new JTabbedPane();
                tabbedPane.addTab("position", createPanel());
                tabbedPane.addTab("change", createPanel());
                tabbedPane.addTab("overview", createPanel());
                frame.add(tabbedPane);
                refreshContentPane();
                frame.setVisible(true);
            } else {
                refreshContentPane();
            }
            frame.validate();
            lock.unlock();
        } catch (Exception e) {
            lock.unlock();
            log.error("create trade log frame failed, error={}", e);
        }
    }

    private JPanel createPanel() {
        JPanel panel = new JPanel(false);
        panel.setPreferredSize(new Dimension(520, 400));
        return panel;
    }

    /**
     * 刷新内容窗格数据
     */
    private void refreshContentPane() {
        Container c = frame.getContentPane();
        // tag窗体内容面板
        JTabbedPane tabbedPane = (JTabbedPane) c.getComponent(0);
        // log、position tab panel
        JPanel positionPanel = (JPanel) tabbedPane.getComponent(0);
        refreshPositionLogPanel(positionPanel);
        // change tab panel
        JPanel changePanel = (JPanel) tabbedPane.getComponent(1);
        refreshChangeLogPanel(changePanel);
        // overview tab panel
        JPanel viewPanel = (JPanel) tabbedPane.getComponent(2);
        refreshOverviewTagPanel(viewPanel);
    }

    /**
     * position 面板刷新
     *
     * @param posLogPanel
     */
    private void refreshPositionLogPanel(JPanel posLogPanel) {
        JScrollPane pricePane;
        JScrollPane highTopPane;
        JScrollPane lowTopPane;
        int componentCount = posLogPanel.getComponentCount();
        JList<JLabel> priceLogList = new JList<>(positionLogModel);
        priceLogList.setCellRenderer(new MyListCellRenderer());
        pricePane = new JScrollPane(priceLogList);
        pricePane.setPreferredSize(new Dimension(520, 160));
        if (componentCount == 0) {
            posLogPanel.add(pricePane, 0);

            JList<JLabel> highTopList = new JList<>(highTopModel);
            highTopList.setCellRenderer(new MyListCellRenderer());
            highTopPane = new JScrollPane(highTopList);
            highTopPane.setPreferredSize(new Dimension(520, 90));
            posLogPanel.add(highTopPane, 1);

            JList<JLabel> lowTopList = new JList<>(lowTopModel);
            lowTopList.setCellRenderer(new MyListCellRenderer());
            lowTopPane = new JScrollPane(lowTopList);
            lowTopPane.setPreferredSize(new Dimension(520, 90));
            posLogPanel.add(lowTopPane, 2);
        } else {
            posLogPanel.remove(0);
            posLogPanel.add(pricePane, 0);
//            pricePane = (JScrollPane) posLogPaneposl.getComponent(0);
//            JList<JLabel> priceLogList = (JList<JLabel>) ((JViewport) pricePane.getComponent(0)).getComponent(0);
//            priceLogList.setModel(positionLogModel);

            highTopPane = (JScrollPane) posLogPanel.getComponent(1);
            JList<JLabel> highTopList = (JList<JLabel>) ((JViewport) highTopPane.getComponent(0)).getComponent(0);
            highTopList.setModel(highTopModel);

            lowTopPane = (JScrollPane) posLogPanel.getComponent(2);
            JList<JLabel> lowTopList = (JList<JLabel>) ((JViewport) lowTopPane.getComponent(0)).getComponent(0);
            lowTopList.setModel(lowTopModel);
        }
    }

    /**
     * price change log
     *
     * @param changeLogPanel
     */
    private void refreshChangeLogPanel(JPanel changeLogPanel) {
        JList<JLabel> priceLogList = new JList<>(changeLogModel);
        priceLogList.setCellRenderer(new MyListCellRenderer());
        JScrollPane changePane = new JScrollPane(priceLogList);
        changePane.setPreferredSize(new Dimension(520, 360));
        changeLogPanel.removeAll();
        changeLogPanel.add(changePane, 0);
    }

    /**
     * @param viewPanel 概况面板刷新
     */
    private void refreshOverviewTagPanel(JPanel viewPanel) {
        // 概况、领涨、领跌 ScrollPane
        JScrollPane overviewPane;
        JScrollPane changeHighTopPane;
        JScrollPane changeLowTopPane;
        int componentCount = viewPanel.getComponentCount();
        if (componentCount == 0) {
            JTextArea overviewTxt = new JTextArea(overview);
            overviewTxt.setEditable(false);
            overviewPane = new JScrollPane(overviewTxt);
            overviewPane.setPreferredSize(new Dimension(520, 160));
            viewPanel.add(overviewPane, 0);

            JList<JLabel> changeHighTopList = new JList<>(changeHighTopModel);
            changeHighTopList.setCellRenderer(new MyListCellRenderer());
            changeHighTopPane = new JScrollPane(changeHighTopList);
            changeHighTopPane.setPreferredSize(new Dimension(520, 90));
            viewPanel.add(changeHighTopPane, 1);

            JList<JLabel> changeLowTopList = new JList<>(changeLowTopModel);
            changeLowTopList.setCellRenderer(new MyListCellRenderer());
            changeLowTopPane = new JScrollPane(changeLowTopList);
            changeLowTopPane.setPreferredSize(new Dimension(520, 90));
            viewPanel.add(changeLowTopPane, 2);
        } else {
            overviewPane = (JScrollPane) viewPanel.getComponent(0);
            JViewport viewport = (JViewport) overviewPane.getComponent(0);
            JTextArea overviewTxtArea = (JTextArea) viewport.getComponent(0);
            overviewTxtArea.setText(overview);

            changeHighTopPane = (JScrollPane) viewPanel.getComponent(1);
            JList<JLabel> changeHighTopList = (JList<JLabel>) ((JViewport) changeHighTopPane.getComponent(0)).getComponent(0);
            changeHighTopList.setModel(changeHighTopModel);

            changeLowTopPane = (JScrollPane) viewPanel.getComponent(2);
            JList<JLabel> changeLowTopList = (JList<JLabel>) ((JViewport) changeLowTopPane.getComponent(0)).getComponent(0);
            changeLowTopList.setModel(changeLowTopModel);
        }
    }

    public static class MyListCellRenderer extends JLabel implements ListCellRenderer<Object> {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            // 设置JLable的文字
            JLabel text = (JLabel) value;
            setText(text.getText());
            if (text.getText().contains("多")
                    || text.getText().contains(LiveDataCache.SYMBOL_UP)) {
                setForeground(Color.RED);
            } else if (text.getText().contains(LiveDataCache.SYMBOL_DOWN)) {
                setForeground(Color.GREEN);
            } else {
                setForeground(Color.BLACK);
            }
            return this;
        }
    }
}
