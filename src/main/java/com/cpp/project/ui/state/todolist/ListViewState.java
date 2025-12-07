package com.cpp.project.ui.state.todolist;

import com.cpp.project.common.entity.TaskStatus;
import com.cpp.project.todolist.dto.ToDoListDTO;
import com.cpp.project.todolist.service.ToDoListService;
import com.cpp.project.ui.component.MessagePanel;
import com.cpp.project.ui.component.SelectionList;
import com.cpp.project.ui.core.ScreenState;
import com.cpp.project.user.dto.UserDTO;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.util.List;

/**
 * State 1: To-Do Lists View
 */
public class ListViewState implements ScreenState {
    private final Screen screen;
    private final UserDTO currentUser;
    private final ToDoListService toDoListService;
    private final List<ToDoListDTO> todoLists;
    private final Runnable closeCallback;

    private final SelectionList<ToDoListDTO> listSelection;
    private final MessagePanel messagePanel;

    public ListViewState(Screen screen, UserDTO currentUser, ToDoListService toDoListService,
                         List<ToDoListDTO> todoLists, Runnable closeCallback) {
        this.screen = screen;
        this.currentUser = currentUser;
        this.toDoListService = toDoListService;
        this.todoLists = todoLists;
        this.closeCallback = closeCallback;

        listSelection = new SelectionList<>("Your To-Do Lists", list -> {
            int completed = (int) list.getTasks().stream()
                    .filter(t -> TaskStatus.COMPLETED.equals(t.getStatus()))
                    .count();
            return list.getName() + " (" + completed + "/" + list.getTasks().size() + " completed)";
        });
        listSelection.setItems(todoLists);
        listSelection.setFocused(true);
        messagePanel = new MessagePanel();
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = screen.getTerminalSize();

        graphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String title = "=== TO-DO LIST MANAGEMENT ===";
        graphics.putString((size.getColumns() - title.length()) / 2, 1, title);

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(3, 3, "F1: Add List | ESC: Back to Main Menu");

        listSelection.render(graphics, 3, 5);

        if (!listSelection.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(3, 17, "Press ENTER to view list details");
        }

        messagePanel.render(graphics, 3, size.getRows() - 2);
    }

    @Override
    public ScreenState handleInput(KeyStroke keyStroke) {
        messagePanel.clear();

        if (keyStroke.getKeyType() == KeyType.F1) {
            return new AddListState(screen, currentUser, toDoListService, this);
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            closeCallback.run();
            return this;
        } else if (keyStroke.getKeyType() == KeyType.Enter && !listSelection.isEmpty()) {
            return new ListDetailsState(screen, currentUser, toDoListService,
                    listSelection.getSelectedItem(), this);
        } else {
            listSelection.handleInput(keyStroke);
            return this;
        }
    }

    @Override
    public String getStateName() {
        return "ListView";
    }

    public void setSuccessMessage(String message) {
        messagePanel.setSuccess(message);
    }
}
