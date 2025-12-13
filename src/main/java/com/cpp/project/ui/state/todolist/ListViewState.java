package com.cpp.project.ui.state.todolist;

import com.cpp.project.common.entity.TaskStatus;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.SelectionList;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.ui.mediator.ToDoListMediator;
import com.cpp.project.ui.util.UILayoutConstants;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.List;

/**
 * State 1: To-Do Lists View
 *
 * Responsibilities:
 * - Display list of all to-do lists
 * - Handle navigation to details or add list
 * - No data ownership - fetches fresh from mediator
 */
public class ListViewState implements ScreenState {
    private final ToDoListMediator mediator;
    private final SelectionList<ToDoListDTO> listSelection;
    private final MessagePanel messagePanel;

    public ListViewState(ToDoListMediator mediator, String successMessage) {
        this.mediator = mediator;

        listSelection = new SelectionList<>("Your To-Do Lists", list -> {
            int completed = (int) list.getTasks().stream()
                    .filter(t -> TaskStatus.COMPLETED.equals(t.getStatus()))
                    .count();
            return list.getName() + " (" + completed + "/" + list.getTasks().size() + " completed)";
        });
        listSelection.setFocused(true);

        messagePanel = new MessagePanel();
        if (successMessage != null) {
            messagePanel.setSuccess(successMessage);
        }
    }

    @Override
    public void onEnter() {
        // Fetch fresh data when entering this state
        List<ToDoListDTO> todoLists = mediator.getAllToDoLists();
        listSelection.setItems(todoLists);
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = graphics.getSize();

        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== TO-DO LIST MANAGEMENT ===";
        graphics.putString((size.getColumns() - title.length()) / 2, UILayoutConstants.TITLE_ROW, title);

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(UILayoutConstants.LEFT_MARGIN, UILayoutConstants.INSTRUCTIONS_ROW, "F1: Add List | ESC: Back to Main Menu");

        listSelection.render(graphics, UILayoutConstants.LEFT_MARGIN, UILayoutConstants.CONTENT_START_ROW);

        if (!listSelection.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(UILayoutConstants.LEFT_MARGIN, 17, "Press ENTER to view list details");
        }

        messagePanel.render(graphics, UILayoutConstants.LEFT_MARGIN, size.getRows() - UILayoutConstants.BOTTOM_MARGIN);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.F1) {
            // Notify mediator - it will create and transition to AddListState
            mediator.onAddNewList();
            return null; // Mediator handles transition
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            mediator.closeScreen();
            return null;
        } else if (keyStroke.getKeyType() == KeyType.Enter && !listSelection.isEmpty()) {
            // Notify mediator to show details
            mediator.onViewListDetails(listSelection.getSelectedItem().getId());
            return null; // Mediator handles transition
        } else {
            listSelection.handleInput(keyStroke);
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "ListView";
    }
}
