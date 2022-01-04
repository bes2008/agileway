package com.jn.agileway.ssh.client.impl.sshj;

import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.langx.annotation.OnClasses;
import net.schmizz.sshj.common.SecurityUtils;

@OnClasses({"net.schmizz.sshj.SSHClient"})
public class SshjConnectionFactory extends AbstractSshConnectionFactory<SshjConnectionConfig> {

    public SshjConnectionFactory(){
        setName("sshj");
    }

    @Override
    protected Class<?> getDefaultConnectionClass() {
        return SshjConnection.class;
    }

    @Override
    protected void postConstructConnection(final SshConnection connection, final SshjConnectionConfig sshConfig) {
        // 确保扩展的算法已经注册
        SecurityUtils.getSecurityProvider();
        setKnownHosts(connection, sshConfig);
    }
/*
    protected void setKnownHosts0(final SshConnection connection, final SshjConnectionConfig sshConfig) {
        List<File> paths = SshConfigs.getKnownHostsFiles(sshConfig.getKnownHostsPath());
        if (paths.isEmpty()) {
            paths = SshConfigs.getKnownHostsFiles(sshConfig.getKnownHostsPath(), false);
        }
        // 确保扩展的算法已经注册
        SecurityUtils.getSecurityProvider();

        if (!paths.isEmpty()) {
            Collects.forEach(paths, new Consumer<File>() {
                @Override
                public void accept(File file) {
                    try {
                        Files.makeFile(file);
                        HostKeyVerifier verifier = new OpenSSHKnownHosts(file) {


                            @Override
                            protected boolean hostKeyUnverifiableAction(String hostname, PublicKey key) {
                                try {
                                    KeyType keyType = KeyType.fromKey(key);
                                    if(keyType==KeyType.UNKNOWN){
                                        return true;
                                    }
                                    List<HostEntry> entries = this.entries();
                                    entries.add(new SimpleEntry(null, hostname, keyType, key));
                                    this.write();
                                    return true;
                                } catch (Throwable ex) {
                                    logger.error(ex.getMessage(), ex);
                                    return false;
                                }
                            }

                            @Override
                            protected boolean hostKeyChangedAction(HostEntry entry, String hostname, PublicKey key) {
                                try {
                                    // 删除整行，我们自用时，每一个Host，一个算法对应一行
                                    List<HostEntry> entries = this.entries();
                                    entries.remove(entry);

                                    // 创建新的entry
                                    KeyType keyType = KeyType.fromKey(key);
                                    HostEntry newEntry = new SimpleEntry(null, hostname, keyType, key);
                                    entries.add(newEntry);

                                    // 重写文件
                                    this.write();
                                    return true;
                                } catch (Throwable ex) {
                                    logger.error(ex.getMessage(), ex);
                                    return false;
                                }
                            }
                        };
                        FromSshHostKeyVerifierAdapter verifierAdapter = new FromSshHostKeyVerifierAdapter(verifier);
                        connection.addHostKeyVerifier(verifierAdapter);
                    } catch (Throwable ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            });

        }
    }

*/
    @Override
    public SshjConnectionConfig newConfig() {
        return new SshjConnectionConfig();
    }
}
