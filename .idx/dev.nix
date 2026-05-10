{ pkgs, ... }: {

  channel = "unstable";
  packages = [
    pkgs.zulu25
    pkgs.maven
    pkgs.google-cloud-sdk
    pkgs.docker
  ];

  services.postgres = {
    enable = true;
    package = pkgs.postgresql_18;
  };

  env = { };
  idx = {
    # Search for the extensions you want on https://open-vsx.org/ and use "publisher.id"
    extensions = [
      "vscjava.vscode-java-pack"
      "redhat.vscode-xml"
      "redhat.vscode-yaml"
      "GitHub.github-vscode-theme"
    ];
    workspace = {
      # Runs when a workspace is first created with this `dev.nix` file
      onCreate = {
        install = "mvn clean install";
      };
      # Runs when a workspace is (re)started
      onStart = {
        run-server = "PORT=3000 mvn spring-boot:run";
      };
    };
  };
}







