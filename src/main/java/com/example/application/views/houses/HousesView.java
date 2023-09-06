package com.example.application.views.houses;

import com.example.application.data.entity.House;
import com.example.application.data.service.HouseService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
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
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Houses")
@Route(value = "houses/:houseID?/:action?(edit)", layout = MainLayout.class)
@Tag("houses-view")
@JsModule("./views/houses/houses-view.ts")
public class HousesView extends LitTemplate implements HasStyle, BeforeEnterObserver {

    private final String HOUSE_ID = "houseID";
    private final String HOUSE_EDIT_ROUTE_TEMPLATE = "houses/%s/edit";

    // This is the Java companion file of a design
    // You can find the design file inside /frontend/views/
    // The design can be easily edited by using Vaadin Designer
    // (vaadin.com/designer)

    @Id
    private Grid<House> grid;

    @Id
    private TextField address;
    @Id
    private TextField capacity;
    @Id
    private TextField manager;

    @Id
    private Button cancel;
    @Id
    private Button save;

    private BeanValidationBinder<House> binder;

    private House house;

    private final HouseService houseService;

    public HousesView(HouseService houseService) {
        this.houseService = houseService;
        addClassNames("houses-view");
        grid.addColumn(House::getAddress).setHeader("Address").setSortProperty("address").setAutoWidth(true);
        grid.addColumn(House::getCapacity).setHeader("Capacity").setSortProperty("capacity").setAutoWidth(true);
        grid.addColumn(House::getManager).setHeader("Manager").setSortProperty("manager").setAutoWidth(true);
        grid.setItems(query -> houseService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(HOUSE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(HousesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(House.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(capacity).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("capacity");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.house == null) {
                    this.house = new House();
                }
                binder.writeBean(this.house);
                houseService.update(this.house);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(HousesView.class);
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
        Optional<Long> houseId = event.getRouteParameters().get(HOUSE_ID).map(Long::parseLong);
        if (houseId.isPresent()) {
            Optional<House> houseFromBackend = houseService.get(houseId.get());
            if (houseFromBackend.isPresent()) {
                populateForm(houseFromBackend.get());
            } else {
                Notification.show(String.format("The requested house was not found, ID = %s", houseId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(HousesView.class);
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

    private void populateForm(House value) {
        this.house = value;
        binder.readBean(this.house);

    }
}
