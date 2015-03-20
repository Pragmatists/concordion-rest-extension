package pl.pragmatists.concordion.rest;

import org.concordion.api.extension.Extensions;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.FixtureExtension;

@RunWith(ConcordionRunner.class)
@Extensions(FixtureExtension.class)
public class SetupDeleteCommandFixture extends SetupHttpMethodCommandFixture {
}
