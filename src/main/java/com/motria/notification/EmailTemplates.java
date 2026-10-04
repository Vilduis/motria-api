package com.motria.notification;

import org.springframework.web.util.HtmlUtils;

/** Plantillas HTML de los correos. Todos los valores dinámicos se escapan para evitar inyección de HTML. */
final class EmailTemplates {

    private static final String LAYOUT = """
            <div style="font-family:Arial,sans-serif;max-width:560px;margin:auto;color:#222">
              %s
              <hr style="border:none;border-top:1px solid #eee">
              <p style="font-size:12px;color:#888">Motria · mensaje automático, no responder.</p>
            </div>
            """;

    private EmailTemplates() {
    }

    static String temporaryPassword(String technicianName, String email, String tempPassword, String workshopName) {
        String body = """
                <h2>Bienvenido a %s 👋</h2>
                <p>Hola <strong>%s</strong>, el administrador del taller te ha creado una cuenta de técnico.</p>
                <p>Estas son tus credenciales temporales para iniciar sesión:</p>
                <table style="border-collapse:collapse;margin:16px 0">
                  <tr><td style="padding:6px 12px;background:#f4f4f4"><b>Email</b></td>
                      <td style="padding:6px 12px;background:#f4f4f4">%s</td></tr>
                  <tr><td style="padding:6px 12px"><b>Contraseña temporal</b></td>
                      <td style="padding:6px 12px"><code style="font-size:15px">%s</code></td></tr>
                </table>
                <p style="color:#b00">Por seguridad debes cambiarla la primera vez que ingreses.</p>
                """.formatted(escape(workshopName), escape(technicianName), escape(email), escape(tempPassword));
        return LAYOUT.formatted(body);
    }

    static String passwordReset(String userName, String resetUrl, long validMinutes) {
        String url = escape(resetUrl);
        String body = """
                <h2>Restablecer contraseña 🔐</h2>
                <p>Hola <strong>%s</strong>, recibimos una solicitud para restablecer tu contraseña en Motria.</p>
                <p>Haz clic en el botón para crear una nueva contraseña. El enlace expira en <b>%d minutos</b>.</p>
                <p style="margin:24px 0">
                  <a href="%s" style="background:#0066cc;color:#fff;padding:12px 24px;text-decoration:none;border-radius:6px;display:inline-block">
                    Restablecer contraseña
                  </a>
                </p>
                <p style="font-size:13px;color:#555">Si el botón no funciona, copia y pega este enlace en tu navegador:</p>
                <p style="font-size:12px;word-break:break-all;color:#0066cc">%s</p>
                <p style="color:#b00;font-size:13px">Si no solicitaste este cambio, ignora este correo. Tu contraseña seguirá igual.</p>
                """.formatted(escape(userName), validMinutes, url, url);
        return LAYOUT.formatted(body);
    }

    private static String escape(String value) {
        return HtmlUtils.htmlEscape(value);
    }
}
