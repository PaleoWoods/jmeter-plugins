package com.blazemeter.jmeter;

import kg.apc.jmeter.JMeterPluginsUtils;
import kg.apc.jmeter.gui.Grid;
import org.apache.jmeter.control.Controller;
import org.apache.jmeter.control.gui.AbstractControllerGui;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.awt.*;
import java.util.LinkedList;

public class WeightedSwitchControllerGui extends AbstractControllerGui {
    private static final Logger log = LoggingManager.getLoggerForClass();
    private static final String WIKIPAGE = "WeightedSwitch";
    public static final String WEIGHTS = "Weight";
    private Grid grid;

    public WeightedSwitchControllerGui() {
        super();
        init();
        clearGui();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        add(JMeterPluginsUtils.addHelpLinkToPanel(makeTitlePanel(), WIKIPAGE), BorderLayout.NORTH);

        grid = new Grid("Child Item Weights",
                new String[]{"Name", WEIGHTS},
                new Class[]{String.class, String.class},
                new String[]{"", "100"});

        add(grid, BorderLayout.CENTER);
    }

    @Override
    public String getLabelResource() {
        return getClass().getCanonicalName();
    }

    @Override
    public String getStaticLabel() {
        return "Weighted Switch Controller";
    }

    @Override
    public TestElement createTestElement() {
        WeightedSwitchController te = new WeightedSwitchController();
        modifyTestElement(te);
        return te;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        if (element instanceof WeightedSwitchController) {
            WeightedSwitchController wsc = (WeightedSwitchController) element;
            wsc.setData(grid.getModel());
        }
    }

    @Override
    public void configure(TestElement element) {
        // from model to GUI
        super.configure(element);

        GuiPackage gp = GuiPackage.getInstance();

        if (gp != null && element instanceof WeightedSwitchController) {
            WeightedSwitchController wsc = (WeightedSwitchController) element;
            CollectionProperty oldData = wsc.getData();

            JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
            JMeterTreeNode root = (JMeterTreeNode) treeModel.getRoot();
            LinkedList<JMeterTreeNode> childItems = getChildItems(root, wsc);
            grid.getModel().clearData();
            for (int n = 0; n < childItems.size(); n++) {
                JMeterTreeNode node = childItems.get(n);
                String w = "100";
                if (oldData.size() > n && oldData.get(n) != null) {
                    JMeterProperty row = oldData.get(n);
                    if (row instanceof CollectionProperty) {
                        w = ((CollectionProperty) row).get(1).getStringValue();
                    }
                }
                grid.getModel().addRow(new String[]{node.getTestElement().getName(), w});
            }
        }
    }

    private LinkedList<JMeterTreeNode> getChildItems(JMeterTreeNode root, WeightedSwitchController element) {
        LinkedList<JMeterTreeNode> result = new LinkedList<>();
        for (int i = 0; i < root.getChildCount(); i++) {
            JMeterTreeNode child = (JMeterTreeNode) root.getChildAt(i);

            TestElement te = child.getTestElement();
            if (element != root.getTestElement()) {
                result.addAll(getChildItems(child, element));
            } else {
                if (te instanceof Sampler || te instanceof Controller) {
                    result.add(child);
                }
            }
        }
        return result;
    }

    @Override
    public void clearGui() {
        super.clearGui();
    }
}
