package com.example.kotlinsupabase.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://mtwdjzplbyvojyfrxleq.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im10d2RqenBsYnl2b2p5ZnJ4bGVxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjMxMDA4MjIsImV4cCI6MjA3ODY3NjgyMn0.euFXhfOLUrlnN80komkBdE-xbI9wLT3CdSKPL92m8VQ"
        ) {
            install(Postgrest)
            install(Auth)
        }
    }
}