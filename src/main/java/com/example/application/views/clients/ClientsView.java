package com.example.application.views.clients;

import com.example.application.data.entity.Client;
import com.example.application.data.service.ClientService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Clients")
@Route(value = "Clients/:clientID?/:action?(edit)", layout = MainLayout.class)
@Tag("clients-view")
@JsModule("./views/clients/clients-view.ts")
public class ClientsView extends LitTemplate implements HasStyle, BeforeEnterObserver {

    private final String CLIENT_ID = "clientID";
    private final String CLIENT_EDIT_ROUTE_TEMPLATE = "Clients/%s/edit";

    // This is the Java companion file of a design
    // You can find the design file inside /frontend/views/
    // The design can be easily edited by using Vaadin Designer
    // (vaadin.com/designer)

    @Id
    private Grid<Client> grid;

    @Id
    private TextField first_name;
    @Id
    private TextField last_name;
    @Id
    private DatePicker dob;
    @Id
    private TextField access_number;
    @Id
    private TextField status;
    @Id
    private TextField house;

    @Id
    private Button cancel;
    @Id
    private Button save;

    private BeanValidationBinder<Client> binder;

    private Client client;

    private final ClientService clientService;

    public ClientsView(ClientService clientService) {
        this.clientService = clientService;
        addClassNames("clients-view");
        grid.addColumn(Client::getFirst_name).setHeader("First_name").setSortProperty("first_name").setAutoWidth(true);
        grid.addColumn(Client::getLast_name).setHeader("Last_name").setSortProperty("last_name").setAutoWidth(true);
        grid.addColumn(Client::getDob).setHeader("Dob").setSortProperty("dob").setAutoWidth(true);
        grid.addColumn(Client::getAccess_number).setHeader("Access_number").setSortProperty("access_number")
                .setAutoWidth(true);
        grid.addColumn(Client::getStatus).setHeader("Status").setSortProperty("status").setAutoWidth(true);
        grid.addColumn(Client::getHouse).setHeader("House").setSortProperty("house").setAutoWidth(true);
        grid.setItems(query -> clientService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(CLIENT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ClientsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Client.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.client == null) {
                    this.client = new Client();
                }
                binder.writeBean(this.client);
                clientService.update(this.client);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(ClientsView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> clientId = event.getRouteParameters().get(CLIENT_ID).map(Long::parseLong);
        if (clientId.isPresent()) {
            Optional<Client> clientFromBackend = clientService.get(clientId.get());
            if (clientFromBackend.isPresent()) {
                populateForm(clientFromBackend.get());
            } else {
                Notification.show(String.format("The requested client was not found, ID = %s", clientId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ClientsView.class);
            }
        }
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Client value) {
        this.client = value;
        binder.readBean(this.client);

    }
}
