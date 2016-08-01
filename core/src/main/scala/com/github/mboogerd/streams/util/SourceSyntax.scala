/*
 * Copyright 2015 Merlijn Boogerd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mboogerd.streams.util

import akka.stream.scaladsl.Source
import com.github.mboogerd.streams.util.Route.RouteSourceOps
import com.github.mboogerd.streams.util.ZipVia.ZipViaSourceOps

import scala.language.implicitConversions

/**
  *
  */
trait SourceSyntax {
  implicit def zipViaOps[S, U](source: Source[S, U]): ZipViaSourceOps[S, U] = new ZipViaSourceOps(source)

  implicit def routeOps[S, U](source: Source[S, U]): RouteSourceOps[S, U] = new RouteSourceOps(source)
}
