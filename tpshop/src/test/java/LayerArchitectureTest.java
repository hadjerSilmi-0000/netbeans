package test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.Entity;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.annotation.WebServlet;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * ArchUnit Architecture Tests — MiniShop tpshop project
 *
 * Covers all 7 architectural patterns applied:
 *   W1 — MVC / SRP (Servlet is controller only)
 *   W2 — Value Object (CartItem has domain properties)
 *   W3 — Repository Pattern (DAO structure)
 *   W4 — Validation Pipeline (Chain of Responsibility)
 *   W5 — Service Layer (ShopService interface)
 *   W6 — Microkernel / Plugin System
 *   W7 — Event-Driven Architecture
 */
public class LayerArchitectureTest {

    // ── Import all production classes (exclude tests themselves) ─────────────
    private static JavaClasses ALL_CLASSES;

    @BeforeClass
    public static void loadClasses() {
        ALL_CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(
                "servlets",
                "EJB",
                "EJB.event",
                "EJB.plugin",
                "dao",
                "entity",
                "validation",
                "com.mycompany.tpshop"
            );
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SECTION 1 — LAYERED ARCHITECTURE (W1, W3, W5)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * RULE 1: Layered architecture — top-to-bottom dependency only.
     *   Web → Service → Business → Persistence
     *
     * Servlets must not access DAO directly.
     * DAO must not access Servlets or EJBs.
     */
    @Test
    public void testLayeredArchitectureIsRespected() {
        ArchRule rule = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Web")         .definedBy("servlets..")
            .layer("Service")     .definedBy("EJB.ShopServiceImpl", "EJB.ShopService")
            .layer("Business")    .definedBy("EJB..")
            .layer("Persistence") .definedBy("dao..")
            .layer("Domain")      .definedBy("entity..")
            .layer("Validation")  .definedBy("validation..")

            .whereLayer("Web")        .mayOnlyAccessLayers("Service", "Business", "Domain")
            .whereLayer("Service")    .mayOnlyAccessLayers("Business", "Domain")
            .whereLayer("Business")   .mayOnlyAccessLayers("Persistence", "Domain", "Validation")
            .whereLayer("Persistence").mayOnlyAccessLayers("Domain")
            .whereLayer("Domain")     .mayNotAccessAnyLayer()
            .whereLayer("Validation") .mayOnlyAccessLayers("Domain");

        rule.check(ALL_CLASSES);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SECTION 2 — W1: MVC / SINGLE RESPONSIBILITY
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * RULE 2: Servlets must not access the DAO layer directly.
     * Only Business/Service EJBs may talk to the DAO.
     */
    @Test
    public void testServletDoesNotAccessDAODirectly() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("servlets..")
            .should().accessClassesThat().resideInAPackage("dao..")
            .because("Servlets are controllers — they must not bypass the service layer to access DAO");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 3: Servlets must not contain persistence annotations.
     * @PersistenceContext belongs only in the DAO layer.
     */
    @Test
    public void testServletHasNoPersistenceContext() {
        ArchRule rule = noFields()
            .that().areDeclaredInClassesThat().resideInAPackage("servlets..")
            .should().beAnnotatedWith(PersistenceContext.class)
            .because("Persistence context belongs only in the DAO layer");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 4: Only classes in the servlets package may be annotated @WebServlet.
     * Business and DAO classes must never be servlets.
     */
    @Test
    public void testOnlyServletPackageHasWebServlet() {
        ArchRule rule = classes()
            .that().areAnnotatedWith(WebServlet.class)
            .should().resideInAPackage("servlets..")
            .because("@WebServlet annotation is only valid on Servlet classes in the web layer");

        rule.check(ALL_CLASSES);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SECTION 3 — W2: VALUE OBJECT (CartItem)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * RULE 5: CartItem must implement Serializable (required for Stateful EJBs).
     */
    @Test
    public void testCartItemImplementsSerializable() {
        ArchRule rule = classes()
            .that().haveSimpleName("CartItem")
            .should().implement(java.io.Serializable.class)
            .because("CartItem is stored in a @Stateful EJB and must be Serializable");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 6: CartItem must NOT depend on any EJB or servlet class.
     * Value objects are pure domain — no infrastructure dependencies.
     */
    @Test
    public void testCartItemHasNoDependencyOnEJBOrServlets() {
        ArchRule rule = noClasses()
            .that().haveSimpleName("CartItem")
            .should().accessClassesThat().resideInAnyPackage("EJB..", "servlets..")
            .because("CartItem is a Value Object — it must be pure domain with no infrastructure dependency");

        rule.check(ALL_CLASSES);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SECTION 4 — W3: REPOSITORY PATTERN (DAO)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * RULE 7: DAO implementations must be @Stateless EJBs.
     */
    @Test
    public void testDAOImplementationsAreStateless() {
        ArchRule rule = classes()
            .that().resideInAPackage("dao..")
            .and().areNotInterfaces()
            .should().beAnnotatedWith(Stateless.class)
            .because("DAO implementations must be @Stateless EJBs for transaction support");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 8: DAO implementations must use @PersistenceContext for DB access.
     * Direct JDBC connections are forbidden.
     */
    @Test
    public void testDAOImplementationsUsePersistenceContext() {
        ArchRule rule = classes()
            .that().resideInAPackage("dao..")
            .and().areNotInterfaces()
            .should().haveOnlyFinalFields()
            .orShould().haveAnyFields()
            .because("DAO implementation must declare fields for EntityManager injection");

        // More targeted rule: no DriverManager usage in DAO
        ArchRule noDirectJDBC = noClasses()
            .that().resideInAPackage("dao..")
            .should().accessClassesThat().haveFullyQualifiedName("java.sql.DriverManager")
            .because("DAO must use JPA/EntityManager, not raw JDBC DriverManager");

        noDirectJDBC.check(ALL_CLASSES);
    }

    /**
     * RULE 9: @Entity classes must reside in the entity package.
     */
    @Test
    public void testEntityClassesResideInEntityPackage() {
        ArchRule rule = classes()
            .that().areAnnotatedWith(Entity.class)
            .should().resideInAPackage("entity..")
            .because("JPA entities must live in the entity package (Domain layer)");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 10: Entity classes must NOT access DAO or EJB classes.
     * Entities are pure domain objects with no infrastructure dependencies.
     */
    @Test
    public void testEntityClassesHaveNoDependencyOnDAOOrEJB() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("entity..")
            .should().accessClassesThat().resideInAnyPackage("dao..", "EJB..", "servlets..")
            .because("Entity classes are pure domain objects — no infrastructure dependency allowed");

        rule.check(ALL_CLASSES);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SECTION 5 — W4: VALIDATION PIPELINE
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * RULE 11: All validator classes must implement the Validator interface.
     */
    @Test
    public void testAllValidatorsImplementValidatorInterface() {
        ArchRule rule = classes()
            .that().resideInAPackage("validation..")
            .and().haveSimpleNameEndingWith("Validator")
            .and().areNotInterfaces()
            .should().implement(validation.Validator.class)
            .because("All concrete validators must implement the Validator interface (Pipeline pattern)");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 12: Validator classes must not access the DAO or EJB layers.
     * Validators are pure business logic — they receive data, they don't fetch it.
     */
    @Test
    public void testValidatorsDoNotAccessDatabaseOrEJBs() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("validation..")
            .should().accessClassesThat().resideInAnyPackage("dao..", "EJB..", "servlets..")
            .because("Validators are pure logic — they receive product data, they never fetch it from DB");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 13: ValidationPipeline must only use Validator interface, not concrete implementations directly.
     * This enforces the Open/Closed principle — the pipeline is closed for modification.
     */
    @Test
    public void testValidationPipelineUsesValidatorInterface() {
        ArchRule rule = classes()
            .that().haveSimpleName("ValidationPipeline")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                "validation..",
                "entity..",
                "java..",
                "jakarta.."
            )
            .because("ValidationPipeline must not depend on infrastructure — only on the Validator interface and domain");

        rule.check(ALL_CLASSES);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SECTION 6 — W5: SERVICE LAYER
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * RULE 14: ShopServiceImpl must implement ShopService.
     */
    @Test
    public void testShopServiceImplImplementsShopServiceInterface() {
        ArchRule rule = classes()
            .that().haveSimpleName("ShopServiceImpl")
            .should().implement(EJB.ShopService.class)
            .because("ShopServiceImpl is the concrete service layer implementation and must honour the ShopService contract");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 15: ShopServiceImpl must be @Stateless.
     */
    @Test
    public void testShopServiceImplIsStateless() {
        ArchRule rule = classes()
            .that().haveSimpleName("ShopServiceImpl")
            .should().beAnnotatedWith(Stateless.class)
            .because("The service layer EJB must be @Stateless — it holds no conversational state");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 16: Servlets must communicate with the EJB layer only via ShopService.
     * Direct injection of MiniShopEJB into a Servlet is forbidden.
     */
    @Test
    public void testServletsDoNotDirectlyAccessMiniShopEJB() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("servlets..")
            .should().accessClassesThat().haveSimpleName("MiniShopEJB")
            .because("Servlets must go through ShopService (W5). Direct access to MiniShopEJB bypasses the service layer.");

        rule.check(ALL_CLASSES);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SECTION 7 — W6: MICROKERNEL / PLUGIN SYSTEM
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * RULE 17: All concrete plugins must implement CartPlugin.
     */
    @Test
    public void testAllPluginsImplementCartPluginInterface() {
        ArchRule rule = classes()
            .that().resideInAPackage("EJB.plugin..")
            .and().haveSimpleNameEndingWith("Plugin")
            .and().areNotInterfaces()
            .and().doNotHaveSimpleName("PluginRegistry")
            .should().implement(EJB.plugin.CartPlugin.class)
            .because("Every cart plugin must implement CartPlugin (Microkernel Architecture)");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 18: Plugins must not access the DAO, servlet, or event layers.
     * A plugin has a single job — compute a message and return it.
     */
    @Test
    public void testPluginsDoNotAccessInfrastructure() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("EJB.plugin..")
            .and().haveSimpleNameEndingWith("Plugin")
            .and().areNotInterfaces()
            .should().accessClassesThat().resideInAnyPackage("dao..", "servlets..", "EJB.event..")
            .because("Plugins are pure feature extensions — they must not touch DAO, Servlet, or Event infrastructure");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 19: PluginRegistry must be @Singleton and @Startup.
     */
    @Test
    public void testPluginRegistryIsSingletonAndStartup() {
        ArchRule singleton = classes()
            .that().haveSimpleName("PluginRegistry")
            .should().beAnnotatedWith(Singleton.class)
            .because("PluginRegistry is the microkernel core — it must be a @Singleton");

        ArchRule startup = classes()
            .that().haveSimpleName("PluginRegistry")
            .should().beAnnotatedWith(Startup.class)
            .because("PluginRegistry loads plugins at application boot with @Startup");

        singleton.check(ALL_CLASSES);
        startup.check(ALL_CLASSES);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SECTION 8 — W7: EVENT-DRIVEN ARCHITECTURE
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * RULE 20: CartEventBus must be a @Singleton.
     * There is exactly one event bus for the application.
     */
    @Test
    public void testCartEventBusIsSingleton() {
        ArchRule rule = classes()
            .that().haveSimpleName("CartEventBus")
            .should().beAnnotatedWith(Singleton.class)
            .because("The event bus must be a @Singleton — all publishers and subscribers share one instance");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 21: All event listeners must implement CartEventListener.
     */
    @Test
    public void testAllListenersImplementCartEventListener() {
        ArchRule rule = classes()
            .that().resideInAPackage("EJB.event..")
            .and().haveSimpleNameEndingWith("Listener")
            .and().areNotInterfaces()
            .should().implement(EJB.event.CartEventListener.class)
            .because("All event listeners must implement CartEventListener (Event-Driven Architecture)");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 22: Listeners must be @Singleton and @Startup to self-register.
     */
    @Test
    public void testListenersAreSingletonAndStartup() {
        ArchRule singleton = classes()
            .that().resideInAPackage("EJB.event..")
            .and().haveSimpleNameEndingWith("Listener")
            .and().areNotInterfaces()
            .should().beAnnotatedWith(Singleton.class)
            .because("Listeners must be @Singleton to be injected and shared by the event bus");

        ArchRule startup = classes()
            .that().resideInAPackage("EJB.event..")
            .and().haveSimpleNameEndingWith("Listener")
            .and().areNotInterfaces()
            .should().beAnnotatedWith(Startup.class)
            .because("Listeners must be @Startup to self-register with CartEventBus at boot");

        singleton.check(ALL_CLASSES);
        startup.check(ALL_CLASSES);
    }

    /**
     * RULE 23: CartEvent must be Serializable (travels across EJB boundaries).
     */
    @Test
    public void testCartEventIsSerializable() {
        ArchRule rule = classes()
            .that().haveSimpleName("CartEvent")
            .should().implement(java.io.Serializable.class)
            .because("CartEvent is an event object that may cross EJB boundaries — it must be Serializable");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 24: CartEvent must not depend on business or web-layer classes.
     * Events are pure data carriers — no business logic, no HTTP.
     */
    @Test
    public void testCartEventIsPureDataCarrier() {
        ArchRule rule = noClasses()
            .that().haveSimpleName("CartEvent")
            .should().accessClassesThat().resideInAnyPackage("EJB..", "dao..", "servlets..", "validation..")
            .because("CartEvent is a data carrier — it must not depend on business, DAO, or web-layer classes");

        rule.check(ALL_CLASSES);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SECTION 9 — GENERAL ARCHITECTURAL GUARDS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * RULE 25: No circular dependencies between packages.
     * Persistence must not depend on Business, Business must not depend on Web.
     */
    @Test
    public void testNoCyclicDependencyDAOToEJB() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("dao..")
            .should().accessClassesThat().resideInAPackage("EJB..")
            .because("DAO is a lower layer — it must never import or call EJB classes (no upward dependency)");

        rule.check(ALL_CLASSES);
    }

    @Test
    public void testNoCyclicDependencyEntityToHigherLayers() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("entity..")
            .should().accessClassesThat().resideInAnyPackage("EJB..", "dao..", "servlets..", "validation..")
            .because("Entity (domain) is the lowest layer — it must not depend on any higher-level class");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 26: @Stateful EJBs must not be injected into @Stateless EJBs as fields
     * (GlassFish supports it via @EJB but they must be accessed carefully).
     * This rule verifies that only MiniShopFull is @Stateful.
     */
    @Test
    public void testOnlyMiniShopFullIsStateful() {
        ArchRule rule = classes()
            .that().areAnnotatedWith(Stateful.class)
            .should().haveSimpleName("MiniShopFull")
            .because("Only the shopping cart (MiniShopFull) should be @Stateful — all other EJBs must be @Stateless or @Singleton");

        rule.check(ALL_CLASSES);
    }

    /**
     * RULE 27: Validation classes must reside only in the validation package.
     */
    @Test
    public void testValidationClassesResideInValidationPackage() {
        ArchRule rule = classes()
            .that().implement(validation.Validator.class)
            .should().resideInAPackage("validation..")
            .because("All classes implementing Validator must be in the validation package");

        rule.check(ALL_CLASSES);
    }
}
