/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package mortgage;

import org.osoa.sca.annotations.Service;

/**
 * A pojo implementation of RiskAssessment service
 */
@Service(RiskAssessment.class)
public class RiskAssessmentImpl implements RiskAssessment {
    public boolean assess(int creditScore, double ratio) {
        if (creditScore >= 750) {
            return ratio < 0.35;
        } else if (creditScore >= 700) {
            return ratio < 0.30;
        } else {
            return ratio < 0.25;
        }
    }
}
