package levviatasenhancedsubtitles.module;

import com.lukflug.panelstudio.component.DraggableComponent;
import com.lukflug.panelstudio.component.IFixedComponent;

import javax.naming.Context;
import java.awt.*;

public class DraggableSubtitleComponent extends DraggableComponent<IFixedComponent> {
    public DraggableSubtitleComponent(IFixedComponent component) {
        super(component);
    }

    @Override
    public void handleMouseDrag(Context context, int x, int y, int button) {
        super.handleMouseDrag(context, x, y, button);
        // Update the subtitle position based on the drag.
        this.getComponent().setPosition(context.getInterface(), new Point(x, y));
    }

    // Implement other necessary methods...
}