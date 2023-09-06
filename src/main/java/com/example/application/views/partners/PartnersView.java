package com.example.application.views.partners;

import com.example.application.data.entity.Partner;
import com.example.application.data.service.PartnerService;
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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Partners")
@Route(value = "partners/:partnerID?/:action?(edit)", layout = MainLayout.class)
@Tag("partners-view")
@JsModule("./views/partners/partners-view.ts")
public class PartnersView extends LitTemplate implements HasStyle, BeforeEnterObserver {

    private final String PARTNER_ID = "partnerID";
    private final String PARTNER_EDIT_ROUTE_TEMPLATE = "partners/%s/edit";

    // This is the Java companion file of a design
    // You can find the design file inside /frontend/views/
    // The design can be easily edited by using Vaadin Designer
    // (vaadin.com/designer)

    @Id
    private Grid<Partner> grid;

    @Id
    private TextField name;
    @Id
    private TextField phone;
    @Id
    private TextField email;

    @Id
    private Button cancel;
    @Id
    private Button save;

    private BeanValidationBinder<Partner> binder;

    private Partner partner;

    private final PartnerService partnerService;

    public PartnersView(PartnerService partnerService) {
        this.partnerService = partnerService;
        addClassNames("partners-view");
        grid.addColumn(Partner::getName).setHeader("Name").setSortProperty("name").setAutoWidth(true);
        grid.addColumn(Partner::getPhone).setHeader("Phone").setSortProperty("phone").setAutoWidth(true);
        grid.addColumn(Partner::getEmail).setHeader("Email").setSortProperty("email").setAutoWidth(true);
        grid.setItems(query -> partnerService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PARTNER_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(PartnersView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Partner.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.partner == null) {
                    this.partner = new Partner();
                }
                binder.writeBean(this.partner);
                partnerService.update(this.partner);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(PartnersView.class);
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
        Optional<Long> partnerId = event.getRouteParameters().get(PARTNER_ID).map(Long::parseLong);
        if (partnerId.isPresent()) {
            Optional<Partner> partnerFromBackend = partnerService.get(partnerId.get());
            if (partnerFromBackend.isPresent()) {
                populateForm(partnerFromBackend.get());
            } else {
                Notification.show(String.format("The requested partner was not found, ID = %s", partnerId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(PartnersView.class);
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

    private void populateForm(Partner value) {
        this.partner = value;
        binder.readBean(this.partner);

    }
}
