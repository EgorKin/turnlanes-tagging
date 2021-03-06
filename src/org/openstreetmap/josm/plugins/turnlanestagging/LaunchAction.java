// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.turnlanestagging;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.SelectionChangedListener;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.layer.LayerManager;
import org.openstreetmap.josm.tools.Shortcut;

/**
 *
 * @author ruben
 */
public class LaunchAction extends JosmAction implements SelectionChangedListener {

    private boolean isLaunch = false;

    public LaunchAction() {
        super(tr("Turn lanes tagging - editor"),
                "turnlanes-tagging",
                tr("Turn lanes tagging - Editor"),
                Shortcut.registerShortcut("edit:turnlanestaggingeditor",
                        tr("Tool: {0}", tr("turn lanes tagging - editor")),
                        KeyEvent.VK_2, Shortcut.ALT_SHIFT),
                true);
        DataSet.addSelectionListener(this);
        setEnabled(false);
        Main.getLayerManager().addLayerChangeListener(new LayerManager.LayerChangeListener() {
            @Override
            public void layerAdded(LayerManager.LayerAddEvent lae) {
            }

            @Override
            public void layerRemoving(LayerManager.LayerRemoveEvent lre) {
                isLaunch = false;
                setEnabled(false);
                if (!GraphicsEnvironment.isHeadless()) {
                    TurnLanesEditorDialog.getInstance().setVisible(false);
                }
            }

            @Override
            public void layerOrderChanged(LayerManager.LayerOrderChangeEvent loce) {
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        isLaunch = true;
        launchEditor();
    }

    @Override
    public void selectionChanged(Collection<? extends OsmPrimitive> newSelection) {
        setEnabled(newSelection != null && newSelection.size() == 1 && isRoad());
        TurnLanesEditorDialog.getInstance().setEnableOK(true);
        if (isLaunch && TurnLanesEditorDialog.getInstance().isVisible()) {
            launchEditor();
        }
        //disable ok buton
        if (!isRoad() || newSelection.size() > 1) {
            TurnLanesEditorDialog.getInstance().setEnableOK(false);
        }
    }

    protected void launchEditor() {
        if (!isEnabled()) {
            return;
        }
        TurnLanesEditorDialog dialog = TurnLanesEditorDialog.getInstance();
        dialog.startEditSession();
        dialog.setVisible(true);
    }

    public boolean isRoad() {
        Collection<OsmPrimitive> selection = Main.getLayerManager().getEditDataSet().getSelected();
        for (OsmPrimitive element : selection) {
            for (String key : element.keySet()) {
                if (key.equals("highway")) {
                    return true;
                }
            }
        }
        return false;
    }
}
