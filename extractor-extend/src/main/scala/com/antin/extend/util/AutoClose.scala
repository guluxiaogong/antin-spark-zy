package com.antin.extend.util

object AutoClose {
  def open[A <: {def close() : Unit}, B](resource: => A)(code: A => B): Option[B] = {
    val r = resource
    try {
      Some(code(r))
    } finally {
      r.close
    }
  }
}
