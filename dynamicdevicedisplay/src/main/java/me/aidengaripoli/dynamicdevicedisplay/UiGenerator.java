package me.aidengaripoli.dynamicdevicedisplay;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;

public class UiGenerator {
    private static final String TAG = "UiGenerator";
    private FragmentManager fragmentManager;
    private XmlParser xmlParser;
    private Context context;

    public UiGenerator(FragmentManager fragmentManager, Context context) {
        this.fragmentManager = fragmentManager;
        this.context = context;
        xmlParser = new XmlParser();
    }

    public LinearLayout generateUi(InputStream inputStream) {
        LinearLayout rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setId(View.generateViewId());
        rootLayout.setGravity(Gravity.CENTER);

        try {
            NodeList groupNodeList = xmlParser.getGroups(inputStream);

            if(groupNodeList == null)
                return rootLayout;

            addTitle(groupNodeList, rootLayout);

            // iterate through all the <group> elements
            for (int i = 1; i < groupNodeList.getLength(); i++) {
                Element element = (Element) groupNodeList.item(i);

                String groupId = xmlParser.getId(element);
                NodeList guiNodeList = xmlParser.getGuiElementsInGroup(element);

                if (guiNodeList.getLength() == 1) {
                    addElementToLayout((Element) guiNodeList.item(0), rootLayout, groupId);
                } else {
                    createGroupOfElements(rootLayout, guiNodeList, groupId);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "ERROR WHILST GENERATING UI: " + e.getMessage());
        }

        return rootLayout;
    }

    private void addTitle(NodeList groupNodeList, LinearLayout rootLayout) {
        Element groupElement = (Element) groupNodeList.item(0);

        String name = xmlParser.getName(groupElement);

        TextView title = new TextView(context);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
        title.setText(name);

        LinearLayout groupLayout = createLinearLayout();
        groupLayout.addView(title);
        rootLayout.addView(groupLayout);
    }

    private void createGroupOfElements(LinearLayout layout, NodeList guiNodeList, String groupId) {
        // Generate a horizontal Linear layout and add the elements to that.
        LinearLayout groupLayout = createLinearLayout();

        // iterate through all the <gui_element> elements in the group
        for (int i = 0; i < guiNodeList.getLength(); i++) {
            Element element = (Element) guiNodeList.item(i);
            addElementToLayout(element, groupLayout, groupId);
        }

        // add new horizontal layout to root layout
        layout.addView(groupLayout);
    }

    private void addElementToLayout(Element element, LinearLayout layout, String groupId) {
        // generate a view (widget) for each gui_element
        Fragment fragment = ElementsFactory.getElement(element);

        String fragmentTag = groupId + "-" + xmlParser.getId(element);

        // add the view to the groups layout
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.add(layout.getId(), fragment, fragmentTag);
            fragmentTransaction.commit();
        }
    }

    private LinearLayout createLinearLayout() {
        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(layoutParams);
        layout.setId(View.generateViewId());

        return layout;
    }
}