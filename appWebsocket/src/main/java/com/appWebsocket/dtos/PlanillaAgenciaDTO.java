package com.appWebsocket.dtos;

import java.math.BigDecimal;
import java.util.List;

public class PlanillaAgenciaDTO {
    private Integer agenciaId;
    private String agenciaNombre;
    private String ciudad;
    private Integer totalEmpleados;
    private BigDecimal gastoTotalPlanilla;
    private BigDecimal sueldoPromedio;
    private List<UsuarioResponseDTO> empleados;

    public PlanillaAgenciaDTO() {}

    public PlanillaAgenciaDTO(Integer agenciaId, String agenciaNombre, String ciudad,
                              Integer totalEmpleados, BigDecimal gastoTotalPlanilla,
                              BigDecimal sueldoPromedio, List<UsuarioResponseDTO> empleados) {
        this.agenciaId = agenciaId;
        this.agenciaNombre = agenciaNombre;
        this.ciudad = ciudad;
        this.totalEmpleados = totalEmpleados;
        this.gastoTotalPlanilla = gastoTotalPlanilla;
        this.sueldoPromedio = sueldoPromedio;
        this.empleados = empleados;
    }

    public Integer getAgenciaId() { return agenciaId; }
    public void setAgenciaId(Integer agenciaId) { this.agenciaId = agenciaId; }

    public String getAgenciaNombre() { return agenciaNombre; }
    public void setAgenciaNombre(String agenciaNombre) { this.agenciaNombre = agenciaNombre; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public Integer getTotalEmpleados() { return totalEmpleados; }
    public void setTotalEmpleados(Integer totalEmpleados) { this.totalEmpleados = totalEmpleados; }

    public BigDecimal getGastoTotalPlanilla() { return gastoTotalPlanilla; }
    public void setGastoTotalPlanilla(BigDecimal gastoTotalPlanilla) { this.gastoTotalPlanilla = gastoTotalPlanilla; }

    public BigDecimal getSueldoPromedio() { return sueldoPromedio; }
    public void setSueldoPromedio(BigDecimal sueldoPromedio) { this.sueldoPromedio = sueldoPromedio; }

    public List<UsuarioResponseDTO> getEmpleados() { return empleados; }
    public void setEmpleados(List<UsuarioResponseDTO> empleados) { this.empleados = empleados; }
}
