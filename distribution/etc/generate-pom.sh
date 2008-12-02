# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License. 

# This script can be used to generate a Maven build profile that includes all
# the modules included directly or transitively in a distribution

cat pom.xml | awk 'BEGIN { i=0 } /<modules>/ { i=1; print } /.*/ { if (i==0) print } '

mvn -o dependency:list | awk '/.INFO.    (.*.tuscany.sca):(tuscany-)(.*):(.*):(.*):(.*)/ { print gensub("(.INFO.    )(.*)(:)(tuscany-)(.*)(:)(.*)(:)(.*)(:)(.*)", "\\5", "g") }' | grep -v "distribution-" | grep -v "maven-" | grep -v "wsdl2java" | grep -v "java2wsdl" | sort | awk '{ printf "                <module>../../../modules/%s</module>\n", $1 }'

cat pom.xml | awk 'BEGIN { i=0 } /<\/modules>/ { i=1 } /.*/ { if (i==1) print } '
