package com.walmartlabs.strati.migrationtools.oneops2k8migration.controller;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.walmartlabs.strati.migrationtools.oneops2k8migration.Oneops2k8migrationApplicationTests;
import com.walmartlabs.strati.migrationtools.oneops2k8migration.dal.KloopzCmDal;
import com.walmartlabs.strati.migrationtools.oneops2k8migration.service.CIAttributesService;
import com.walmartlabs.strati.migrationtools.oneops2k8migration.util.MigrationUtil;
import com.walmartlabs.strati.migrationtools.oneops2k8migration.util.OOPhases;
import com.walmartlabs.strati.migrationtools.oneops2k8migration.util.Platform;

/**
 * @author dsing17
 *
 */
public class CIAttributesSvcControllerTest extends Oneops2k8migrationApplicationTests {

	private final Logger log = LoggerFactory.getLogger(getClass());



	@MockBean
	KloopzCmDal dal;
	
	@MockBean
	private CIAttributesService svc;

	@MockBean
	private MigrationUtil util;

	@MockBean
	Platform platform;

	@InjectMocks
	CIAttributesSvcController controller;
	
	@Before
	public void init() {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void getCiAttributesForTomcatMigrationTest() throws Exception {

		
		String orgName = "TestOrg";
		String assemblyName = "TestAssembly";
		String platformName = "TestPlatform";
		String envName = "dev";

		platform.setOrgName(orgName);
		platform.setAssemblyName(assemblyName);
		platform.setPlatformName(platformName);
		platform.setEnvName(envName);

		String ns = util.buildNsPath(orgName, assemblyName);
		platform.setNs(ns);
		platform.setNsForPlatformCiComponents(
				new MigrationUtil().getnsForPlatformCiComponents(ns, platformName, OOPhases.OPERATE, envName));
		
		
		HashMap<String, String> bomTomcatCiAttributesMap = new HashMap<>();

		bomTomcatCiAttributesMap.put("tomcatAttribName1", "tomcatAttribValue1");
		bomTomcatCiAttributesMap.put("tomcatAttribName2", "tomcatAttribValue2");

		HashMap<String, String> bomArtifactCiAttributesMap = new HashMap<>();

		bomArtifactCiAttributesMap.put("ArtifactAppAttribName1", "ArtifactAppAttribValue1");
		bomArtifactCiAttributesMap.put("ArtifactAppAttribName2", "ArtifactAppAttribValue2");

		Map<String, String> platAttribsMapForTomcatAndArtifactCI = new HashMap<>();

		platAttribsMapForTomcatAndArtifactCI.putAll(bomTomcatCiAttributesMap);
		platAttribsMapForTomcatAndArtifactCI.putAll(bomArtifactCiAttributesMap);

		when(svc.getPlatAttribsMapForTomcatAndArtifactCI(platform))
				.thenReturn(platAttribsMapForTomcatAndArtifactCI);

		String mapObjectToYaml = new MigrationUtil().yamlifyObject(platAttribsMapForTomcatAndArtifactCI);

		when(util.yamlifyObject(platAttribsMapForTomcatAndArtifactCI)).thenReturn(mapObjectToYaml);

		String actualStringifiedYamlResponse=controller.getCiAttributesForTomcatMigration(orgName, assemblyName, platformName, envName);

		Assert.assertEquals(mapObjectToYaml, actualStringifiedYamlResponse);
		
	}

}
