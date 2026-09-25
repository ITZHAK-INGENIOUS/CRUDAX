package com.crudax.launcher

import com.crudax.launcher.cruda.ActionRisk
import com.crudax.launcher.cruda.CrudaAction
import com.crudax.launcher.cruda.risk
import org.junit.Assert.assertEquals
import org.junit.Test

class CrudaActionTest {
    @Test
    fun readFileIsLowRisk() {
        assertEquals(ActionRisk.LOW, CrudaAction.ReadFile("/tmp/x").risk())
    }

    @Test
    fun deleteFileIsHighRisk() {
        assertEquals(ActionRisk.HIGH, CrudaAction.DeleteFile("/tmp/x").risk())
    }

    @Test
    fun createFileIsMediumRisk() {
        assertEquals(ActionRisk.MEDIUM, CrudaAction.CreateFile("/tmp/x").risk())
    }
}
