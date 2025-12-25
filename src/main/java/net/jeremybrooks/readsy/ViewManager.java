/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2025  Jeremy Brooks
 *
 * This file is part of readsy.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jeremybrooks.readsy;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;
import net.jeremybrooks.readsy.controllers.BooksController;
import net.jeremybrooks.readsy.controllers.EditController;
import net.jeremybrooks.readsy.controllers.EditStartController;
import net.jeremybrooks.readsy.controllers.WelcomeController;
import net.jeremybrooks.readsy.model.AppModel;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;

public class ViewManager {

    private final AppModel appModel ;
    private final Callback<Class<?>, Object> controllerFactory ;

    private final ReadOnlyObjectWrapper<Parent> currentView = new ReadOnlyObjectWrapper<>();
    public Parent getCurrentView() {
        return currentView.get();
    }
    public ReadOnlyObjectProperty<Parent> currentViewProperty() {
        return currentView.getReadOnlyProperty();
    }

    public ViewManager(AppModel appModel) {
        this.appModel = appModel;

        // The controller factory is a functional interface that maps Class<?> instances
        // to objects. It is used by the FXMLLoader to create controllers for a given
        // controller class (specified by fx:controller in the FXML file).
        // Since our controllers don't have default constructors, we need a controller
        // factory to determine how to instantiate the controllers.
        // The implementation here looks for a constructor with a single parameter of type Model,
        // and if it finds one invokes that constructor, providing the model instance.
        controllerFactory = type -> {
            try {
                for (Constructor<?> c : type.getConstructors()) {
                    if (c.getParameterCount() == 1 && c.getParameterTypes()[0].equals(AppModel.class)) {
                        return c.newInstance(appModel);
                    }
                }
                return type.getConstructor().newInstance();
            } catch (Exception e) {
                throw e instanceof RuntimeException re ? re : new RuntimeException(e);
            }
        };

        // update the current view if either the user or the browse state change:
        ChangeListener<Object> listener = (obs, oldValue, newValue) -> updateView();
        appModel.activeStateProperty().addListener(listener);

        // And initialize the current view based on the current state of the model:
        updateView();
    }

    private void updateView() {
        try {
            // get the FXML file to load
            URL resource = switch(appModel.getActiveState()) {
                case WELCOME -> WelcomeController.class.getResource("/net/jeremybrooks/readsy/gui/Welcome.fxml");
                case BOOKS -> BooksController.class.getResource("/net/jeremybrooks/readsy/gui/Books.fxml");
                case NEW_BOOK -> EditStartController.class.getResource("/net/jeremybrooks/readsy/gui/EditStart.fxml");
                case EDIT_BOOK -> EditController.class.getResource("/net/jeremybrooks/readsy/gui/Edit.fxml");
            };

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(controllerFactory);
            currentView.set(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
