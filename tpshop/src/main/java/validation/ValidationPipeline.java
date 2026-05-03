package validation;

import entity.Product;
import java.util.Arrays;
import java.util.List;

public class ValidationPipeline {

    private final List<Validator> earlyStages;
    private final List<Validator> lateStages;

    public ValidationPipeline() {
        // Stage 1 and 2 only - no product needed
        this.earlyStages = Arrays.asList(
            new NullCheckValidator(),
            new FormatValidator()
        );
        // Stage 3 and 4 only - product needed
        this.lateStages = Arrays.asList(
            new ExistenceValidator(),
            new AvailabilityValidator()
        );
    }

    public String runEarly(String productParam) {
        for (Validator stage : earlyStages) {
            String error = stage.validate(productParam, null);
            if (error != null) return error;
        }
        return null;
    }

    public String runLate(String productParam, Product product) {
        for (Validator stage : lateStages) {
            String error = stage.validate(productParam, product);
            if (error != null) return error;
        }
        return null;
    }
}