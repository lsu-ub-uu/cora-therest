package se.uu.ub.cora.therest.fitnesse;

import fitnesse.junit.FitNesseRunner;
import org.junit.runner.RunWith;

@RunWith(FitNesseRunner.class)
@FitNesseRunner.Suite("TheRestTests")
@FitNesseRunner.FitnesseDir(".")
@FitNesseRunner.OutputDir("./target/fitnesse-results")
public class ITFitNesseRunner
{
}