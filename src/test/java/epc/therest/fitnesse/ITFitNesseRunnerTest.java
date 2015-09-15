package epc.therest.fitnesse;

/**
 * TODO: Class description
 *
 * @author <a href="mailto:uwe.klosa@ub.uu.se">Uwe Klosa</a>
 */

import fitnesse.junit.FitNesseRunner;
import org.junit.runner.RunWith;

@RunWith(FitNesseRunner.class)
@FitNesseRunner.Suite("RestSuite")
@FitNesseRunner.FitnesseDir(".")
@FitNesseRunner.OutputDir("./target/fitnesse-results")
public class ITFitNesseRunnerTest
{
}