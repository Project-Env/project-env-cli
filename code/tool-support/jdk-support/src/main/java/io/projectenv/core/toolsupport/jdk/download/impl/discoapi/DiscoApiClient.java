package io.projectenv.core.toolsupport.jdk.download.impl.discoapi;

import java.io.IOException;
import java.util.List;

public interface DiscoApiClient {

    DiscoApiResult<List<DiscoApiJdkPackage>> getJdkPackages(String version,
                                                            String distro,
                                                            String architecture,
                                                            String archiveType,
                                                            String operatingSystem) throws IOException;

    DiscoApiResult<List<DiscoApiJdkPackageDetails>> getJdkPackageDetails(String ephemeralId) throws IOException;

}
